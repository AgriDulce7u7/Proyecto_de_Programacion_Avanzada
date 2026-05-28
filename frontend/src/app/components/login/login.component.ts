import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  standalone: false,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  correo = '';
  password = '';
  error = '';
  cargando = false;

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.cargando = true;
    this.error = '';
    this.auth.login(this.correo, this.password).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.error = 'Correo o contraseña incorrectos.';
        this.cargando = false;
      }
    });
  }
}
