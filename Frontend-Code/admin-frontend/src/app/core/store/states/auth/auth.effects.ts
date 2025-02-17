import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { map, mergeMap, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import * as AuthActions from './auth.actions';

@Injectable()
export class AuthEffects {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly actions$ = inject(Actions);
  
  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      mergeMap(action =>
        this.authService
          .login({ email: action.email, password: action.password })
          .pipe(
            map(response => AuthActions.loginSuccess({ response })),
            catchError(error => of(AuthActions.loginFailure({ error: error.message })))
          )
      )
    )
  );

  loginSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.loginSuccess),
        tap(({ response }) => {
          localStorage.setItem('auth_token', response.token);
          if (response.passwordResetRequired) {
            this.router.navigate(['/auth/reset-password']);
          } else {
            this.router.navigate(['/admin/dashboard']);
          }
        })
      ),
    { dispatch: false }
  );

  resetPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.resetPassword),
      mergeMap(action =>
        this.authService
          .resetPassword(action.oldPassword, action.newPassword)
          .pipe(
            map(() => AuthActions.resetPasswordSuccess()),
            catchError(error => of(AuthActions.resetPasswordFailure({ error: error.message })))
          )
      )
    )
  );

  resetPasswordSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.resetPasswordSuccess),
        tap(() => {
          this.router.navigate(['/auth/login']);
        })
      ),
    { dispatch: false }
  );

  logout$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.logout),
        tap(() => {
          this.authService.logout();
          this.router.navigate(['/auth/login']);
        })
      ),
    { dispatch: false }
  );

  // constructor(
  //   private readonly actions$: Actions,
  //   private readonly authService: AuthService,
  //   private readonly router: Router
  // ) {}
}
