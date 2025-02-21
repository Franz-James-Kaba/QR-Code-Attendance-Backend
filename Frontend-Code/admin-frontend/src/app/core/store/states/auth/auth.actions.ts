import { AuthResponse } from '@app/shared/models/auth.model';
import { createAction,  props } from '@ngrx/store';

export const AuthActions = {
  login: createAction('[Auth] Login', props<{ email: string; password: string }>()),
  loginSuccess: createAction('[Auth] Login Success', props<{ response: AuthResponse }>()),
  loginFailure: createAction('[Auth] Login Failure', props<{ error: string }>()),
  logout: createAction('[Auth] Logout'),
  resetPassword: createAction('[Auth] Reset Password', props<{ oldPassword: string; newPassword: string }>()),
  resetPasswordSuccess: createAction('[Auth] Reset Password Success'),
  resetPasswordFailure: createAction('[Auth] Reset Password Failure', props<{ error: string }>()),
  clearError: createAction('[Auth] Clear Error')
};
