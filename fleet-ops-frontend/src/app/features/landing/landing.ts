import { Component, ElementRef, AfterViewInit, ViewChild } from '@angular/core';
import { LucideFileText, LucideShield, LucideCar, LucideWrench, LucideBuilding2, LucideZap, LucideRefreshCw, LucideCircleCheckBig, LucideLockKeyhole } from '@lucide/angular';

@Component({
  selector: 'app-landing',
  imports: [
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
  templateUrl: './landing.html',
  styleUrl: './landing.scss'
})
export class Landing implements AfterViewInit {
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