import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'authToken';
  private readonly nombreKey = 'authNombre';

  readonly isAuthenticated = signal(!!localStorage.getItem(this.tokenKey));

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string) {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, { email, password }).pipe(
      tap(res => {
        localStorage.setItem(this.tokenKey, res.token);
        localStorage.setItem(this.nombreKey, res.nombre);
        this.isAuthenticated.set(true);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.nombreKey);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getNombre(): string {
    return localStorage.getItem(this.nombreKey) || 'Don Eleuterio';
  }
}
