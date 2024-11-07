// sidebar.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside
      class="bg-gray-800 text-gray-100 min-h-screen transition-all duration-300"
      [class.w-64]="isOpen"
      [class.w-16]="!isOpen"
      [class.translate-x-0]="isOpen || !isMobile"
      [class.-translate-x-full]="!isOpen && isMobile"
    >
      <nav class="flex flex-col h-full py-5">
        <div class="px-4 mb-6">
          <img [class.w-8]="!isOpen" class="w-32 transition-all" src="assets/logo.png" alt="Logo">
        </div>

        <div class="flex-1 px-2 space-y-1">
          <a routerLink="/dashboard"
             routerLinkActive="bg-gray-900 text-white"
             class="flex items-center px-2 py-2 text-sm font-medium rounded-md text-gray-300 hover:bg-gray-700 hover:text-white transition-colors group">
            <svg class="mr-3 h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
            </svg>
            <span [class.opacity-0]="!isOpen" class="transition-opacity">Dashboard</span>
          </a>
         <a routerLink="/users"
            routerLinkActive="bg-gray-900 text-white"
            class="flex items-center px-2 py-2 text-sm font-medium rounded-md text-gray-300 hover:bg-gray-700 hover:text-white transition-colors group">
          <svg class="mr-3 h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
          </svg>
          <span [class.opacity-0]="!isOpen" class="transition-opacity">Users</span>
        </a>
        </div>
      </nav>
    </aside>
  `,
  styles: [`
    :host {
      display: block;
      height: 100%;
    }
  `]
})
export class SidebarComponent {
  @Input() isOpen = true;
  @Input() isMobile = false;
}
