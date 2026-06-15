import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthResponse, CurrentUser, LoginRequest, RegisterRequest, UserRole } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/auth-service/api/auth';
  private readonly tokenKey = 'savia_token';

  readonly currentUser = signal<CurrentUser | null>(null);

  constructor(private readonly http: HttpClient) {
    if (this.getToken()) {
      this.loadCurrentUser().subscribe({
        error: () => this.logout()
      });
    }
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => {
        this.saveToken(response.token);
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap((response) => {
        this.saveToken(response.token);
      })
    );
  }

  loadCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${this.apiUrl}/me`).pipe(
      tap((user) => this.currentUser.set(user))
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.currentUser.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(...roles: UserRole[]): boolean {
    const user = this.currentUser();
    return !!user && roles.includes(user.role);
  }

  private saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }
}