import { AfterViewInit, Component, ElementRef, inject, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, finalize, forkJoin, of, tap } from 'rxjs';
import { Chart, registerables } from 'chart.js';
import { StatisticsService } from '../../../core/services/statistics.service';
import { ProviderStatistics, OutcomeStatistics, VolumeStatistics } from '../../../core/models/statistics.model';
import { CompanyService } from '../../../core/services/company.service';
import { Company } from '../../../core/models/company.model';

Chart.register(...registerables);

@Component({
  selector: 'app-statistics',
  imports: [FormsModule],
  templateUrl: './statistics.component.html',
  styleUrl: './statistics.component.scss',
})
export class StatisticsComponent implements AfterViewInit {
  private readonly statisticsService = inject(StatisticsService);
  private readonly companyService = inject(CompanyService);

  @ViewChild('volumeCanvas') volumeCanvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('providerCanvas') providerCanvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('outcomeCanvas') outcomeCanvasRef!: ElementRef<HTMLCanvasElement>;

  private volumeChart?: Chart;
  private providerChart?: Chart;
  private outcomeChart?: Chart;

  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly companies = signal<Company[]>([]);
  readonly selectedCompanyId = signal<string>('');

  readonly fromDate = signal<string>(this.defaultFrom());
  readonly toDate = signal<string>(this.defaultTo());

  readonly volumePeriod = signal<'daily' | 'weekly'>('daily');

  private providerStats?: ProviderStatistics;
  private outcomeStats?: OutcomeStatistics;
  private volumeStats?: VolumeStatistics;

  ngAfterViewInit(): void {
    this.loadCompanies();
    this.loadStatistics();
  }

  loadCompanies(): void {
    this.companyService
      .getCompanies({ size: 100 })
      .pipe(
        tap((response) => this.companies.set(response.content)),
        catchError(() => of(null))
      )
      .subscribe();
  }

  loadStatistics(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const range = { from: this.toIsoDateTime(this.fromDate()), to: this.toIsoDateTime(this.toDate()) };

    forkJoin({
      providers: this.statisticsService.getProviderStatistics(range),
      outcomes: this.statisticsService.getOutcomeStatistics(range),
      volume: this.statisticsService.getVolumeStatistics(range),
    })
      .pipe(
        tap((result) => {
          this.providerStats = result.providers;
          this.outcomeStats = result.outcomes;
          this.volumeStats = result.volume;
          this.renderCharts();
        }),
        catchError(() => {
          this.errorMessage.set('Failed to load statistics. Please try again.');
          return of(null);
        }),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe();
  }

  onFilterChange(): void {
    this.loadStatistics();
  }

  onVolumePeriodChange(period: 'daily' | 'weekly'): void {
    this.volumePeriod.set(period);
    this.renderVolumeChart();
  }

  private renderCharts(): void {
    this.renderVolumeChart();
    this.renderProviderChart();
    this.renderOutcomeChart();
  }

  private renderVolumeChart(): void {
    if (!this.volumeStats) return;

    const companyId = this.selectedCompanyId();
    let labels: string[];
    let values: number[];

    if (this.volumePeriod() === 'daily') {
      const dailyData = companyId
        ? (this.volumeStats.dailyByCompany[companyId] ?? {})
        : this.volumeStats.dailyTotal;
      labels = Object.keys(dailyData).sort();
      values = labels.map((label) => dailyData[label]);
    } else {
      const weeklyData = companyId
        ? (this.volumeStats.weeklyByCompany[companyId] ?? [])
        : this.volumeStats.weeklyTotal;
      const sorted = [...weeklyData].sort((a, b) => a.weekStart.localeCompare(b.weekStart));
      labels = sorted.map((entry) => entry.weekStart);
      values = sorted.map((entry) => entry.count);
    }

    this.volumeChart?.destroy();
    this.volumeChart = new Chart(this.volumeCanvasRef.nativeElement, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: this.volumePeriod() === 'daily' ? 'Searches per day' : 'Searches per week',
            data: values,
            borderColor: '#a78bfa',
            backgroundColor: 'rgba(124, 58, 237, 0.15)',
            fill: true,
            tension: 0.3,
          },
        ],
      },
      options: this.chartOptions(),
    });
  }

  private renderProviderChart(): void {
    if (!this.providerStats) return;

    const companyId = this.selectedCompanyId();
    const data = companyId
      ? (this.providerStats.byCompanyAndProvider[companyId] ?? { FREE: 0, PREMIUM: 0 })
      : this.providerStats.totalByProvider;

    this.providerChart?.destroy();
    this.providerChart = new Chart(this.providerCanvasRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels: ['Free', 'Premium'],
        datasets: [
          {
            data: [data.FREE, data.PREMIUM],
            backgroundColor: ['#a78bfa', '#f5a623'],
            borderWidth: 0,
          },
        ],
      },
      options: { ...this.chartOptions(), plugins: { legend: { labels: { color: '#f8f7fb' } } } },
    });
  }

  private renderOutcomeChart(): void {
    if (!this.outcomeStats) return;

    const companyId = this.selectedCompanyId();
    const data = companyId
      ? (this.outcomeStats.byCompanyAndOutcome[companyId] ?? { FOUND: 0, NO_RESULTS: 0, THIRD_PARTY_DOWN: 0 })
      : this.outcomeStats.totalByOutcome;

    this.outcomeChart?.destroy();
    this.outcomeChart = new Chart(this.outcomeCanvasRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ['Found', 'No results', 'Third-party down'],
        datasets: [
          {
            label: 'Searches',
            data: [data.FOUND, data.NO_RESULTS, data.THIRD_PARTY_DOWN],
            backgroundColor: ['#4ade80', '#94a3b8', '#f87171'],
            borderRadius: 6,
          },
        ],
      },
      options: this.chartOptions(),
    });
  }

  private chartOptions(): any {
    return {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: {
        x: { ticks: { color: '#b9aed4' }, grid: { color: 'rgba(167, 139, 250, 0.1)' } },
        y: { ticks: { color: '#b9aed4' }, grid: { color: 'rgba(167, 139, 250, 0.1)' } },
      },
    };
  }

  private toIsoDateTime(date: string): string {
    return `${date}T00:00:00`;
  }

  private defaultFrom(): string {
    const date = new Date();
    date.setMonth(date.getMonth() - 1);
    return date.toISOString().slice(0, 10);
  }

  private defaultTo(): string {
    return new Date().toISOString().slice(0, 10);
  }
}