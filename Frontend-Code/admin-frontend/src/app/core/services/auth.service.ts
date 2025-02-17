import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, delay } from 'rxjs';
import { AuthResponse, LoginCredentials } from '../../shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'api/auth'; // Replace with actual API URL

  constructor(private readonly http: HttpClient) {}

  // Dummy login implementation
  login(credentials: LoginCredentials): Observable<AuthResponse> {
    // Simulate API call
    return of({
      token: 'dummy_token_123',
      passwordResetRequired: Math.random() > 0.5, // Randomly require password reset
      role: 'ADMIN'
    }).pipe(delay(1000)); // Simulate network delay
  }

  resetPassword(oldPassword: string, newPassword: string): Observable<void> {
    return of(void 0).pipe(delay(1000));
  }

  logout(): void {
    localStorage.removeItem('auth_token');
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }
}
