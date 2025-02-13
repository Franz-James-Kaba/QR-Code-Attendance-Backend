export interface AuthResponse {
  token: string;
  passwordResetRequired: boolean;
  role: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}
