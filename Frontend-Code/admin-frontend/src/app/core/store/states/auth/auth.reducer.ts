import { createReducer, on } from '@ngrx/store';
import * as AuthActions from './auth.actions';

export interface AuthState {
  token: string | null;
  passwordResetRequired: boolean;
  role: string | null;
  isLoading: boolean;
  error: string | null;
}

export const initialState: AuthState = {
  token: null,
  passwordResetRequired: false,
  role: null,
  isLoading: false,
  error: null
};

export const authReducer = createReducer(
  initialState,
  on(AuthActions.login, state => ({
    ...state,
    isLoading: true,
    error: null
  })),
  on(AuthActions.loginSuccess, (state, { response }) => ({
    ...state,
    token: response.token,
    passwordResetRequired: response.passwordResetRequired,
    role: response.role,
    isLoading: false
  })),
  on(AuthActions.loginFailure, (state, { error }) => ({
    ...state,
    error,
    isLoading: false
  })),
  on(AuthActions.logout, () => initialState),
  on(AuthActions.resetPasswordSuccess, state => ({
    ...state,
    passwordResetRequired: false
  }))
);
