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
    <div class="min-h-screen flex">
      <app-sidebar
        [isOpen]="sidebarOpen"
        [isMobile]="isMobile"
      ></app-sidebar>

      <div class="flex-1 flex flex-col">
        <app-header
          (toggleSidebar)="toggleSidebar()"
        ></app-header>

        <main class="flex-1 p-6 bg-gray-50">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      height: 100vh;
    }
    .grid-cols-layout {
      display: grid;
      grid-template-columns: auto 1fr;
    }
  `]
})
export class LayoutComponent {
  sidebarOpen = true;
  isMobile = window.innerWidth < 1024;

  @HostListener('window:resize')
  onResize() {
    this.isMobile = window.innerWidth < 1024;
    if (!this.isMobile) {
      this.sidebarOpen = true;
    }
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }
}
