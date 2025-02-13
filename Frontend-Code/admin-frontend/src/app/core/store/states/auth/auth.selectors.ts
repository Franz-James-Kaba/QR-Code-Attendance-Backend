import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AuthState } from './auth.reducer';

export const selectAuthState = createFeatureSelector<AuthState>('auth');

export const selectIsAuthenticated = createSelector(
  selectAuthState,
  auth => !!auth.token
);

export const selectPasswordResetRequired = createSelector(
  selectAuthState,
  auth => auth.passwordResetRequired
);

export const selectAuthError = createSelector(
  selectAuthState,
  auth => auth.error
);

export const selectIsLoading = createSelector(
  selectAuthState,
  auth => auth.isLoading
);
