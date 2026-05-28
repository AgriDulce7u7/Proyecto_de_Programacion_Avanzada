import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SolicitudService } from '../../services/solicitud.service';
import { SugerenciaClasificacion } from '../../models/solicitud.model';

@Component({
  standalone: false,
  selector: 'app-nueva-solicitud',
  templateUrl: './nueva-solicitud.component.html',
  styleUrls: ['./solicitudes.component.css']
})
export class NuevaSolicitudComponent {
  form = {
    tipo: '', descripcion: '', canal: '',
    solicitanteId: '', solicitanteNombre: '', solicitanteEmail: '',
    impactoGrado: false, fechaLimite: ''
  };
  sugerencia?: SugerenciaClasificacion;
  cargandoIA = false;
  cargando = false;
  error = '';

  tipos   = ['REGISTRO_ASIGNATURA','HOMOLOGACION','CANCELACION_ASIGNATURA','SOLICITUD_CUPO','CONSULTA_ACADEMICA'];
  canales = ['CSU','CORREO','SAC','TELEFONICO','PRESENCIAL'];

  constructor(private solicitudService: SolicitudService, private router: Router) {}

  sugerirTipo() {
    if (!this.form.descripcion || this.form.descripcion.length < 10) return;
    this.cargandoIA = true;
    this.solicitudService.sugerirClasificacion(this.form.descripcion).subscribe({
      next: s => { this.sugerencia = s; this.form.tipo = s.tipoSugerido; this.cargandoIA = false; },
      error: () => this.cargandoIA = false
    });
  }

  registrar() {
    this.cargando = true;
    const payload: any = { ...this.form };
    if (!payload.fechaLimite) delete payload.fechaLimite;
    this.solicitudService.registrar(payload).subscribe({
      next: s => this.router.navigate(['/solicitudes', s.id]),
      error: err => { this.error = err?.error?.mensaje || 'Error al registrar la solicitud.'; this.cargando = false; }
    });
  }

  volver() { this.router.navigate(['/dashboard']); }
}
