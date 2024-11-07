// layout.component.ts
import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, HeaderComponent, SidebarComponent, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-100">
      <!-- Backdrop for mobile -->
      <div *ngIf="isMobile && sidebarOpen"
           class="fixed inset-0 bg-black bg-opacity-50 z-40"
           (click)="toggleSidebar()">
      </div>

      <div class="flex">
        <!-- Sidebar -->
        <app-sidebar
          [isOpen]="sidebarOpen"
          [isMobile]="isMobile"
        ></app-sidebar>

        <!-- Main Content -->
        <div class="flex-1 flex flex-col min-h-screen"
             [class.lg:ml-64]="sidebarOpen && !isMobile"
             [class.lg:ml-16]="!sidebarOpen && !isMobile">
          <app-header
            (toggleSidebar)="toggleSidebar()"
          ></app-header>

          <main class="flex-1 p-6">
            <router-outlet></router-outlet>
          </main>
        </div>
      </div>
    </div>
  `
})
export class LayoutComponent {
  sidebarOpen = true;
  isMobile = window.innerWidth < 1024;

  @HostListener('window:resize')
  onResize() {
    const wasNotMobile = !this.isMobile;
    this.isMobile = window.innerWidth < 1024;

    // If transitioning from mobile to desktop
    if (wasNotMobile && !this.isMobile) {
      this.sidebarOpen = true;
    }
    // If transitioning to mobile
    else if (!wasNotMobile && this.isMobile) {
      this.sidebarOpen = false;
    }
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }
}
