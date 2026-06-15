export type UserRole = 'CLIENT' | 'AGENT' | 'TECHNICIAN' | 'MANAGER' | 'ADMIN';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstname: string;
  lastname: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  firstname: string;
  lastname: string;
  email: string;
  role: UserRole;
}

export interface CurrentUser {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
  role: UserRole;
  enabled: boolean;
}