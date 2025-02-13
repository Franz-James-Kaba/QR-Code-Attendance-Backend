import { createAction, props } from '@ngrx/store';
import { AuthResponse } from '../../../../shared/models/auth.model';

export const login = createAction(
  '[Auth] Login',
  props<{ email: string; password: string }>()
);

export const loginSuccess = createAction(
  '[Auth] Login Success',
  props<{ response: AuthResponse }>()
);

export const loginFailure = createAction(
  '[Auth] Login Failure',
  props<{ error: string }>()
);

export const logout = createAction('[Auth] Logout');

export const resetPassword = createAction(
  '[Auth] Reset Password',
  props<{ oldPassword: string; newPassword: string }>()
);

export const resetPasswordSuccess = createAction('[Auth] Reset Password Success');

export const resetPasswordFailure = createAction(
  '[Auth] Reset Password Failure',
  props<{ error: string }>()
);
