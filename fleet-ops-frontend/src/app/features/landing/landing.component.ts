import { Component, ElementRef, AfterViewInit, ViewChild } from '@angular/core';
import { LucideFileText, LucideShield, LucideCar, LucideWrench, LucideBuilding2, LucideZap, LucideRefreshCw, LucideCircleCheckBig, LucideLockKeyhole } from '@lucide/angular';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  imports: [
    RouterLink,
    LucideFileText,
    LucideShield,
    LucideCar,
    LucideWrench,
    LucideBuilding2,
    LucideZap,
    LucideRefreshCw,
    LucideCircleCheckBig,
    LucideLockKeyhole
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent implements AfterViewInit {
  @ViewChild('carReveal') carReveal!: ElementRef<HTMLElement>;

  ngAfterViewInit(): void {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');
          }
        });
      },
      { threshold: 0.2 }
    );
    observer.observe(this.carReveal.nativeElement);
  }
}