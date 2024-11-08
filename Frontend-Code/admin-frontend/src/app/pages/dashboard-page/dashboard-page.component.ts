// dashboard-page.component.ts
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';

interface StatCard {
  title: string;
  value: string | number;
  icon: string;
  iconBg: string;
  iconColor: string;
  trend?: {
    value: string;
    direction: 'up' | 'down';
    text: string;
  };
}

interface Activity {
  student: string;
  class: string;
  time: string;
  status: 'Present' | 'Late' | 'Absent';
}

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatIconModule, RouterLink],
  template: `
    <div class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div *ngFor="let stat of stats"
             class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">{{stat.title}}</p>
              <p class="text-2xl font-semibold">{{stat.value}}</p>
            </div>
            <div [class]="stat.iconBg + ' p-3 rounded-full'">
              <mat-icon [class]="stat.iconColor">{{stat.icon}}</mat-icon>
            </div>
          </div>
          <div *ngIf="stat.trend" class="mt-4 flex items-center text-sm">
            <span [class]="getTrendClass(stat.trend.direction)" class="flex items-center">
              <mat-icon>{{getTrendIcon(stat.trend.direction)}}</mat-icon>
              {{stat.trend.value}}
            </span>
            <span class="text-gray-400 ml-2">{{stat.trend.text}}</span>
          </div>
        </div>
      </div>

      <!-- Recent Activity -->
      <div class="bg-white rounded-lg shadow">
        <div class="p-6 border-b border-gray-200">
          <h2 class="text-lg font-semibold">Recent Activity</h2>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th *ngFor="let header of activityHeaders"
                    class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  {{header}}
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr *ngFor="let activity of activities">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="h-8 w-8 rounded-full bg-gray-200"></div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">{{activity.student}}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{activity.class}}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{activity.time}}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span [class]="getStatusClass(activity.status)">{{activity.status}}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class DashboardPageComponent {
  readonly stats: StatCard[] = [
    {
      title: 'Total Students',
      value: '2,546',
      icon: 'school',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
      trend: { value: '12%', direction: 'up', text: 'vs last month' }
    },
    {
      title: 'Attendance Rate',
      value: '85.4%',
      icon: 'check_circle',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
      trend: { value: '3.2%', direction: 'up', text: 'vs last week' }
    },
    {
      title: 'Active Classes',
      value: '124',
      icon: 'class',
      iconBg: 'bg-purple-100',
      iconColor: 'text-purple-600'
    },
    {
      title: 'Absent Today',
      value: '24',
      icon: 'warning',
      iconBg: 'bg-red-100',
      iconColor: 'text-red-600'
    }
  ];

  readonly activityHeaders = ['Student', 'Class', 'Time', 'Status'];

  readonly activities: Activity[] = [
    { student: 'John Doe', class: 'Mathematics 101', time: '10:30 AM', status: 'Present' },
    { student: 'Jane Smith', class: 'Physics 202', time: '11:15 AM', status: 'Late' }
  ];

  private readonly statusClasses = {
    Present: 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800',
    Late: 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800',
    Absent: 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800'
  };

  getStatusClass(status: string): string {
    return this.statusClasses[status as keyof typeof this.statusClasses];
  }

  getTrendClass(direction: 'up' | 'down'): string {
    return direction === 'up' ? 'text-green-500' : 'text-red-500';
  }

  getTrendIcon(direction: 'up' | 'down'): string {
    return direction === 'up' ? 'arrow_upward' : 'arrow_downward';
  }
}
