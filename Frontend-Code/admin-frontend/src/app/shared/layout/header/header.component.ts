// header.component.ts
import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatBadgeModule
  ],
  template: `
    <header class="bg-white shadow-sm border-b h-16">
      <div class="h-full px-4 flex items-center justify-between">
        <div class="flex items-center">
          <button mat-icon-button (click)="toggleSidebar.emit()" class="lg:hidden">
            <mat-icon>menu</mat-icon>
          </button>
          <h1 class="text-xl font-semibold ml-2">Dashboard</h1>
        </div>

        <div class="flex items-center space-x-2">
          <!-- Notifications -->
          <button mat-icon-button [matMenuTriggerFor]="notificationMenu">
            <mat-icon [matBadge]="'3'" matBadgeColor="warn" matBadgeSize="small">
              notifications
            </mat-icon>
          </button>
          <mat-menu #notificationMenu="matMenu" class="w-80">
            <!-- Add notification items here -->
            <div class="p-4">
              <div class="text-sm">Your notifications here</div>
            </div>
          </mat-menu>

          <!-- Profile -->
          <button mat-button [matMenuTriggerFor]="profileMenu" class="flex items-center">
            <img class="h-8 w-8 rounded-full object-cover" src="assets/avatar.jpg" alt="User avatar">
            <mat-icon class="ml-1">arrow_drop_down</mat-icon>
          </button>
          <mat-menu #profileMenu="matMenu">
            <button mat-menu-item>
              <mat-icon>person</mat-icon>
              <span>Profile</span>
            </button>
            <button mat-menu-item>
              <mat-icon>settings</mat-icon>
              <span>Settings</span>
            </button>
            <button mat-menu-item>
              <mat-icon>logout</mat-icon>
              <span>Logout</span>
            </button>
          </mat-menu>
        </div>
      </div>
    </header>
  `
})
export class HeaderComponent {
  @Output() toggleSidebar = new EventEmitter<void>();
}
