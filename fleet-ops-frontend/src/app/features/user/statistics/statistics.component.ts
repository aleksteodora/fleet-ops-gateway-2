import { AfterViewInit, Component, ElementRef, inject, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, finalize, of, tap } from 'rxjs';
import { Chart, registerables } from 'chart.js';
import { StatisticsService } from '../../../core/services/statistics.service';
import { UserVolumeStatistics } from '../../../core/models/statistics.model';

Chart.register(...registerables);

@Component({
  selector: 'app-statistics',
  imports: [FormsModule],
  templateUrl: './statistics.component.html',
  styleUrl: './statistics.component.scss',
})
export class StatisticsComponent implements AfterViewInit {
  private readonly statisticsService = inject(StatisticsService);

  @ViewChild('volumeCanvas') volumeCanvasRef!: ElementRef<HTMLCanvasElement>;

  private volumeChart?: Chart;

  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly fromDate = signal<string>(this.defaultFrom());
  readonly toDate = signal<string>(this.defaultTo());

  readonly volumePeriod = signal<'daily' | 'weekly'>('daily');

  private stats?: UserVolumeStatistics;

  ngAfterViewInit(): void {
    this.loadStatistics();
  }

  loadStatistics(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const range = { from: this.toIsoDateTime(this.fromDate()), to: this.toIsoDateTime(this.toDate()) };

    this.statisticsService
      .getMySearchStatistics(range)
      .pipe(
        tap((result) => {
          this.stats = result;
          this.renderChart();
        }),
        catchError(() => {
          this.errorMessage.set('Failed to load your statistics. Please try again.');
          return of(null);
        }),
        finalize(() => this.isLoading.set(false))
      )
      .subscribe();
  }

  onFilterChange(): void {
    this.loadStatistics();
  }

  onPeriodChange(period: 'daily' | 'weekly'): void {
    this.volumePeriod.set(period);
    this.renderChart();
  }

  private renderChart(): void {
    if (!this.stats) return;

    let labels: string[];
    let values: number[];

    if (this.volumePeriod() === 'daily') {
      labels = Object.keys(this.stats.daily).sort();
      values = labels.map((label) => this.stats!.daily[label]);
    } else {
      const sorted = [...this.stats.weekly].sort((a, b) => a.weekStart.localeCompare(b.weekStart));
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
            label: this.volumePeriod() === 'daily' ? 'Your searches per day' : 'Your searches per week',
            data: values,
            borderColor: '#a78bfa',
            backgroundColor: 'rgba(124, 58, 237, 0.15)',
            fill: true,
            tension: 0.3,
          },
        ],
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          x: { ticks: { color: '#b9aed4' }, grid: { color: 'rgba(167, 139, 250, 0.1)' } },
          y: { ticks: { color: '#b9aed4' }, grid: { color: 'rgba(167, 139, 250, 0.1)' } },
        },
      },
    });
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