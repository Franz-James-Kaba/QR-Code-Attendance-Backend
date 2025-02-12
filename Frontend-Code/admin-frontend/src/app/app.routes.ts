import { Routes } from '@angular/router';
import { LayoutComponent } from './layouts/auth-layout/layout.component';
import { LayoutComponent as AdminLayoutComponent } from './layouts/admin-layout/layout.component';

export const routes: Routes = [
  {
    path: 'auth',
    component: LayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
      {
        path: 'login',
        loadComponent: () => import('./features/auth/pages/login/login.component').then(m => m.LoginComponent)
      }
    ]
  },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/pages/dashboard/dashboard.component').then(m => m.DashboardComponent)
      }
    ]
  },
  {
    path: '',      // Redirect root to auth/login
    redirectTo: 'auth/login',
    pathMatch: 'full'
  }
];
