import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, delay } from 'rxjs';
import { AuthResponse, LoginCredentials, User } from '../../shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'api/auth';

  constructor(private readonly http: HttpClient) {}

  login(credentials: LoginCredentials): Observable<AuthResponse> {
    // Simulate API call with correct response shape
    return of({
      user: {
        id: 'dummy_id_123',
        email: credentials.email,
        role: 'ADMIN'
      },
      token: 'dummy_token_123',
      passwordResetRequired: Math.random() > 0.5
    }).pipe(delay(1000));
  }

  resetPassword(oldPassword: string, newPassword: string): Observable<void> {
    // TODO: Implement actual password reset logic
    return this.http.post<void>(`${this.API_URL}/reset-password`, {
      oldPassword,
      newPassword
    });
  }

  forgotPassword(email: string): Observable<void> {
    console.log('Forgot password email:', email);
    return of(undefined).pipe(delay(1000));
  }

  logout(): void {
    localStorage.removeItem('auth_token');
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }
}
