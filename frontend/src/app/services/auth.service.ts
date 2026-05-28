import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient, private router: Router) {}

  login(correo: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API}/login`, { correo, password }).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('rol', res.rol);
        localStorage.setItem('nombre', res.nombre);
        localStorage.setItem('id', String(res.id));
      })
    );
  }

  logout(): void { localStorage.clear(); this.router.navigate(['/login']); }
  getToken(): string | null  { return localStorage.getItem('token'); }
  getRol(): string | null    { return localStorage.getItem('rol'); }
  getNombre(): string | null { return localStorage.getItem('nombre'); }
  estaAutenticado(): boolean { return !!this.getToken(); }
  esAdmin(): boolean         { return this.getRol() === 'ADMINISTRATIVO'; }
}
