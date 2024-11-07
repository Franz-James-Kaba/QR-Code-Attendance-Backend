// header.component.ts
import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="bg-white shadow-sm border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <button
              (click)="toggleSidebar.emit()"
              class="p-2 rounded-md text-gray-500 hover:text-gray-900 hover:bg-gray-100 focus:outline-none"
            >
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
              </svg>
            </button>
          </div>

          <div class="flex items-center">
            <div class="flex items-center space-x-4">
              <!-- Notifications -->
              <div class="relative">
                <button
                  (click)="toggleNotifications()"
                  class="p-2 rounded-full text-gray-500 hover:text-gray-900 hover:bg-gray-100 focus:outline-none"
                >
                  <span class="absolute top-0 right-0 h-2 w-2 bg-red-500 rounded-full"></span>
                  <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                  </svg>
                </button>

                <div *ngIf="showNotifications" class="absolute right-0 mt-2 w-80 bg-white rounded-md shadow-lg py-1">
                  <!-- Notifications dropdown content -->
                </div>
              </div>

              <!-- Profile dropdown -->
              <div class="relative">
                <button
                  (click)="toggleProfile()"
                  class="flex items-center space-x-2 p-2 rounded-md hover:bg-gray-100"
                >
                  <img class="h-8 w-8 rounded-full object-cover" src="assets/avatar.jpg" alt="User avatar">
                  <span class="hidden md:block font-medium text-gray-700">John Doe</span>
                </button>

                <div *ngIf="showProfile" class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1">
                  <!-- Profile dropdown content -->
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
  `
})
export class HeaderComponent {
  @Output() toggleSidebar = new EventEmitter<void>();
  showNotifications = false;
  showProfile = false;

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    this.showProfile = false;
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
    this.showNotifications = false;
  }
}
