// sidebar.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

interface NavItem {
  path: string;
  icon: string;
  label: string;
  badge?: number;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  template: `
    <aside
      class="bg-gray-800 text-gray-100 fixed h-full transition-all duration-300 z-40"
      [class.w-64]="isOpen"
      [class.w-16]="!isOpen"
      [class.translate-x-0]="isOpen || !isMobile"
      [class.-translate-x-full]="!isOpen && isMobile"
    >
      <div class="flex flex-col h-full">
        <div class="flex items-center justify-center h-16 border-b border-gray-700">
          <img [class.w-8]="!isOpen"
               [class.w-32]="isOpen"
               class="transition-all"
               src="assets/logo.png"
               alt="Logo">
        </div>

        <nav class="flex-1 py-4">
          <div class="px-2 space-y-1">
            <ng-container *ngFor="let item of navItems">
              <a [routerLink]="item.path"
                 routerLinkActive="bg-gray-900 text-white"
                 class="flex items-center px-2 py-2 text-sm font-medium rounded-md text-gray-300 hover:bg-gray-700 hover:text-white transition-colors group relative"
              >
                <mat-icon>{{ item.icon }}</mat-icon>
                <span [class.opacity-0]="!isOpen"
                      [class.w-0]="!isOpen"
                      class="ml-3 transition-all duration-300">
                  {{item.label}}
                </span>
                <!-- <span *ngIf="item.badge"
                      class="absolute right-2 inline-block px-2 py-0.5 text-xs font-medium rounded-full bg-red-500">
                  {{item.badge}}
                </span> -->
              </a>
            </ng-container>
          </div>
        </nav>

        <div class="p-4 border-t border-gray-700">
          <div class="flex items-center"
               [class.justify-center]="!isOpen">
            <img class="h-8 w-8 rounded-full"
                 [src]="userAvatar"
                 alt="User avatar">
            <div [class.opacity-0]="!isOpen"
                 [class.w-0]="!isOpen"
                 class="ml-3 transition-all duration-300">
              <p class="text-sm font-medium text-white">{{userName}}</p>
              <p class="text-xs text-gray-400">{{userRole}}</p>
            </div>
          </div>
        </div>
      </div>
    </aside>
  `
})
export class SidebarComponent {
  @Input() isOpen = true;
  @Input() isMobile = false;

  userName = 'Dave Devs';
  userRole = 'Administrator';
  userAvatar = 'assets/avatar.jpg';

  navItems: NavItem[] = [
    { path: '/dashboard', icon: 'dashboard', label: 'Dashboard' },
    { path: '/users', icon: 'group', label: 'Users', badge: 3 },
    { path: '/analytics', icon: 'bar_chart', label: 'Analytics' },
    { path: '/tasks', icon: 'assignment', label: 'Tasks', badge: 5 },
    { path: '/settings', icon: 'settings', label: 'Settings' }
  ];
}
