// user-management-page.component.ts
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, PageEvent, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { User } from './user.interface';

@Component({
  selector: 'app-user-management-page',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    MatSelectModule,
    MatMenuModule,
    MatTooltipModule,
    MatDialogModule,
    MatDividerModule,
    MatDialogModule
  ],
  template: `
    <div class="min-h-screen bg-gray-50 p-4 md:p-6">
      <!-- Header with Breadcrumbs -->
      <div class="mb-6">
        <div class="flex items-center text-gray-500 text-sm mb-2">
          <mat-icon class="text-gray-400 h-4 w-4 mr-1">dashboard</mat-icon>
          <span>Dashboard</span>
          <mat-icon class="mx-2 h-4 w-4">chevron_right</mat-icon>
          <span class="text-gray-800">User Management</span>
        </div>
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">User Management</h1>
            <p class="text-gray-600 mt-1">Manage NSPs and Facilitators</p>
          </div>
          <button mat-raised-button color="primary" (click)="openUserDialog()"
                  class="!rounded-lg !px-4 !py-2 flex items-center">
            <mat-icon class="mr-2">person_add</mat-icon>
            Add New User
          </button>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="bg-white rounded-lg shadow p-4 flex items-center">
          <div class="rounded-full bg-blue-100 p-3 mr-4">
            <mat-icon class="text-blue-600">supervisor_account</mat-icon>
          </div>
          <div>
            <p class="text-gray-600 text-sm">Total Users</p>
            <p class="text-xl font-semibold">{{dataSource.data.length}}</p>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex items-center">
          <div class="rounded-full bg-green-100 p-3 mr-4">
            <mat-icon class="text-green-600">domain</mat-icon>
          </div>
          <div>
            <p class="text-gray-600 text-sm">Active NSPs</p>
            <p class="text-xl font-semibold">{{getNSPCount()}}</p>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex items-center">
          <div class="rounded-full bg-purple-100 p-3 mr-4">
            <mat-icon class="text-purple-600">groups</mat-icon>
          </div>
          <div>
            <p class="text-gray-600 text-sm">Facilitators</p>
            <p class="text-xl font-semibold">{{getFacilitatorCount()}}</p>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex items-center">
          <div class="rounded-full bg-orange-100 p-3 mr-4">
            <mat-icon class="text-orange-600">pending_actions</mat-icon>
          </div>
          <div>
            <p class="text-gray-600 text-sm">Pending Approval</p>
            <p class="text-xl font-semibold">{{getPendingCount()}}</p>
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <mat-form-field class="w-full" appearance="outline">
            <mat-label>Search Users</mat-label>
            <mat-icon matPrefix class="mr-2 text-gray-400">search</mat-icon>
            <input matInput (keyup)="applyFilter($event)" placeholder="Name, email, organization...">
          </mat-form-field>

          <mat-form-field class="w-full" appearance="outline">
            <mat-label>User Type</mat-label>
            <mat-select (selectionChange)="filterByRole($event)">
              <mat-option value="">All Users</mat-option>
              <mat-option value="nsp">NSP</mat-option>
              <mat-option value="facilitator">Facilitator</mat-option>
            </mat-select>
            <mat-icon matPrefix class="mr-2 text-gray-400">category</mat-icon>
          </mat-form-field>

          <mat-form-field class="w-full" appearance="outline">
            <mat-label>Status</mat-label>
            <mat-select (selectionChange)="filterByStatus($event)">
              <mat-option value="">All Statuses</mat-option>
              <mat-option value="active">Active</mat-option>
              <mat-option value="pending">Pending</mat-option>
              <mat-option value="suspended">Suspended</mat-option>
            </mat-select>
            <mat-icon matPrefix class="mr-2 text-gray-400">radio_button_checked</mat-icon>
          </mat-form-field>
        </div>
      </div>

      <!-- Users Table -->
      <div class="bg-white rounded-lg shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
          <table mat-table [dataSource]="dataSource" matSort class="w-full">
            <!-- Name Column -->
            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="!bg-gray-50">
                <span class="font-semibold">Name</span>
              </th>
              <td mat-cell *matCellDef="let user" class="!py-4">
                <div class="flex items-center">
                  <div class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center mr-3">
                    <mat-icon class="text-gray-600">person</mat-icon>
                  </div>
                  <div>
                    <div class="font-medium text-gray-900">{{user.name}}</div>
                    <div class="text-sm text-gray-500">{{user.email}}</div>
                  </div>
                </div>
              </td>
            </ng-container>

            <!-- Organization Column -->
            <ng-container matColumnDef="organization">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="!bg-gray-50">
                <span class="font-semibold">Organization</span>
              </th>
              <td mat-cell *matCellDef="let user">
                <div class="flex items-center">
                  <mat-icon class="mr-2 text-gray-400">business</mat-icon>
                  {{user.organization}}
                </div>
              </td>
            </ng-container>

            <!-- Role Column -->
            <ng-container matColumnDef="role">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="!bg-gray-50">
                <span class="font-semibold">Type</span>
              </th>
              <td mat-cell *matCellDef="let user">
                <span class="px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full"
                      [ngClass]="{
                        'bg-blue-100 text-blue-800': user.role === 'nsp',
                        'bg-purple-100 text-purple-800': user.role === 'facilitator'
                      }">
                  <mat-icon class="h-4 w-4 mr-1">{{user.role === 'nsp' ? 'domain' : 'groups'}}</mat-icon>
                  {{user.role | titlecase}}
                </span>
              </td>
            </ng-container>

            <!-- Status Column -->
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="!bg-gray-50">
                <span class="font-semibold">Status</span>
              </th>
              <td mat-cell *matCellDef="let user">
                <span class="px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full"
                      [ngClass]="{
                        'bg-green-100 text-green-800': user.status === 'active',
                        'bg-yellow-100 text-yellow-800': user.status === 'pending',
                        'bg-red-100 text-red-800': user.status === 'suspended'
                      }">
                  {{user.status | titlecase}}
                </span>
              </td>
            </ng-container>

            <!-- Last Login Column -->
            <ng-container matColumnDef="lastLogin">
              <th mat-header-cell *matHeaderCellDef mat-sort-header class="!bg-gray-50">
                <span class="font-semibold">Last Login</span>
              </th>
              <td mat-cell *matCellDef="let user">
                <div class="flex items-center text-gray-500">
                  <mat-icon class="h-4 w-4 mr-1">access_time</mat-icon>
                  {{user.lastLogin | date:'medium'}}
                </div>
              </td>
            </ng-container>

            <!-- Actions Column -->
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef class="!bg-gray-50">
                <span class="font-semibold">Actions</span>
              </th>
              <td mat-cell *matCellDef="let user">
                <button mat-icon-button [matMenuTriggerFor]="menu"
                        class="!text-gray-600 hover:!bg-gray-100">
                  <mat-icon>more_vert</mat-icon>
                </button>
                <mat-menu #menu="matMenu">
                  <button mat-menu-item (click)="editUser(user)">
                    <mat-icon class="text-blue-600">edit</mat-icon>
                    <span>Edit Details</span>
                  </button>
                  <button mat-menu-item (click)="viewUserDetails(user)">
                    <mat-icon class="text-gray-600">visibility</mat-icon>
                    <span>View Details</span>
                  </button>
                  <button mat-menu-item *ngIf="user.status !== 'suspended'"
                          (click)="suspendUser(user)">
                    <mat-icon class="text-orange-600">pause_circle</mat-icon>
                    <span>Suspend User</span>
                  </button>
                  <button mat-menu-item *ngIf="user.status === 'suspended'"
                          (click)="activateUser(user)">
                    <mat-icon class="text-green-600">play_circle</mat-icon>
                    <span>Activate User</span>
                  </button>
                  <mat-divider></mat-divider>
                  <button mat-menu-item (click)="deleteUser(user)" class="text-red-600">
                    <mat-icon class="text-red-600">delete</mat-icon>
                    <span>Delete User</span>
                  </button>
                </mat-menu>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns" class="!bg-gray-50"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"
                class="hover:bg-gray-50 transition-colors duration-150 ease-in-out"></tr>
          </table>
        </div>

        <mat-paginator [pageSizeOptions]="[5, 10, 25, 50]"
                      showFirstLastButtons
                      aria-label="Select page of users"
                      class="border-t">
        </mat-paginator>
      </div>
    </div>
  `
})
export class UserManagementPageComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = ['name', 'organization', 'role', 'status', 'lastLogin', 'actions'];
  dataSource: MatTableDataSource<User>;

  constructor(private dialog: MatDialog) {
    this.dataSource = new MatTableDataSource<User>([
      {
        id: '1',
        name: 'John Doe',
        email: 'john@nsp.com',
        organization: 'Tech Solutions NSP',
        role: 'nsp',
        status: 'active',
        lastLogin: new Date()
      },
      {
        id: '2',
        name: 'Jane Smith',
        email: 'jane@facilitator.com',
        organization: 'Digital Training Corp',
        role: 'facilitator',
        status: 'active',
        lastLogin: new Date()
      },
      // Add more sample data as needed
    ]);
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  getNSPCount(): number {
    return this.dataSource.data.filter(user => user.role === 'nsp').length;
  }
  getFacilitatorCount(): number {
    return this.dataSource.data.filter(user => user.role === 'facilitator').length;
  }

  getPendingCount(): number {
    return this.dataSource.data.filter(user => user.status === 'pending').length;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  filterByRole(event: any) {
    const filterValue = event.value;
    this.dataSource.filterPredicate = (data: User, filter: string) => {
      if (!filter) return true;
      return data.role.toLowerCase() === filter.toLowerCase();
    };
    this.dataSource.filter = filterValue;
  }

  filterByStatus(event: any) {
    const filterValue = event.value;
    this.dataSource.filterPredicate = (data: User, filter: string) => {
      if (!filter) return true;
      return data.status.toLowerCase() === filter.toLowerCase();
    };
    this.dataSource.filter = filterValue;
  }

  openUserDialog(user?: User) {
    // Implement dialog for creating/editing users
    console.log('Opening user dialog', user);
  }

  editUser(user: User) {
    this.openUserDialog(user);
  }

  viewUserDetails(user: User) {
    // Implement view details functionality
    console.log('Viewing user details', user);
  }

  suspendUser(user: User) {
    // Implement user suspension
    console.log('Suspending user', user);
  }

  activateUser(user: User) {
    // Implement user activation
    console.log('Activating user', user);
  }

  deleteUser(user: User) {
    // Implement delete confirmation dialog
    console.log('Deleting user', user);
  }

  // Custom filter predicate for complex filtering
  setupFilterPredicate() {
    this.dataSource.filterPredicate = (data: User, filter: string) => {
      const searchStr = filter.toLowerCase();
      return data.name.toLowerCase().includes(searchStr) ||
             data.email.toLowerCase().includes(searchStr) ||
             data.organization.toLowerCase().includes(searchStr) ||
             data.role.toLowerCase().includes(searchStr) ||
             data.status.toLowerCase().includes(searchStr);
    };
  }
}
