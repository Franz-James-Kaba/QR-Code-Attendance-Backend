import { Routes, Router } from '@angular/router';
import { LayoutComponent } from './layouts/auth-layout/layout.component';
import { LayoutComponent as AdminLayoutComponent } from './layouts/admin-layout/layout.component';
import { AuthGuard } from './core/guards/auth.guard';
import { inject } from '@angular/core';

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
        loadComponent: () => import('./features/auth/pages/login/login.component')
          .then(m => m.LoginComponent),
        // canActivate: [() => {
        //   const router = inject(Router);
        //   const token = localStorage.getItem('auth_token');
        //   if (token) {
        //     router.navigate(['/admin/dashboard']);
        //     return false;
        //   }
        //   return true;
        // }]
      },
      {
        path: 'reset-password',
        loadComponent: () => import('./features/auth/pages/reset-password/reset-password.component')
          .then(m => m.ResetPasswordComponent)
      },
      {
        path: 'unauthorized',
        loadComponent: () => import('./shared/components/unauthorized/unauthorized.component')
          .then(m => m.UnauthorizedComponent)
      }
    ]
  },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/pages/dashboard/dashboard.component')
          .then(m => m.DashboardComponent),
        data: { title: 'Dashboard' }
      },
      {
        path: '**',
        loadComponent: () => import('./shared/components/not-found/not-found.component')
          .then(m => m.NotFoundComponent)
      }
    ]
  },
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found.component')
      .then(m => m.NotFoundComponent)
  }
];
