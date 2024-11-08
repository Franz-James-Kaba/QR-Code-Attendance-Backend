// dashboard-page.component.ts
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatIconModule, RouterLink],
  template: `
    <div class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Total Students</p>
              <p class="text-2xl font-semibold">2,546</p>
            </div>
            <div class="bg-blue-100 p-3 rounded-full">
              <mat-icon class="text-blue-600">school</mat-icon>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="text-green-500 flex items-center">
              <mat-icon>arrow_upward</mat-icon> 12%
            </span>
            <span class="text-gray-400 ml-2">vs last month</span>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Attendance Rate</p>
              <p class="text-2xl font-semibold">85.4%</p>
            </div>
            <div class="bg-green-100 p-3 rounded-full">
              <mat-icon class="text-green-600">check_circle</mat-icon>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="text-green-500 flex items-center">
              <mat-icon>arrow_upward</mat-icon> 3.2%
            </span>
            <span class="text-gray-400 ml-2">vs last week</span>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Active Classes</p>
              <p class="text-2xl font-semibold">124</p>
            </div>
            <div class="bg-purple-100 p-3 rounded-full">
              <mat-icon class="text-purple-600">class</mat-icon>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="text-gray-400">Current semester</span>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Absent Today</p>
              <p class="text-2xl font-semibold">24</p>
            </div>
            <div class="bg-red-100 p-3 rounded-full">
              <mat-icon class="text-red-600">warning</mat-icon>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span class="text-red-500">Requires attention</span>
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
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Class</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Time</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr *ngFor="let activity of recentActivity">
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
                  <span [class]="activity.statusClass">{{activity.status}}</span>
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
  recentActivity = [
    {
      student: 'John Doe',
      class: 'Mathematics 101',
      time: '10:30 AM',
      status: 'Present',
      statusClass: 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800'
    },
    {
      student: 'Jane Smith',
      class: 'Physics 202',
      time: '11:15 AM',
      status: 'Late',
      statusClass: 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800'
    },
    // Add more activity items as needed
  ];
}
