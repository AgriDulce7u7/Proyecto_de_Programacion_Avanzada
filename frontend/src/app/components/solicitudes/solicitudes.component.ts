import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../services/solicitud.service';
import { AuthService } from '../../services/auth.service';
import { SolicitudDetalle, SugerenciaClasificacion, ResumenSolicitud } from '../../models/solicitud.model';

@Component({
  standalone: false,
  selector: 'app-solicitudes',
  templateUrl: './solicitudes.component.html',
  styleUrls: ['./solicitudes.component.css']
})
export class SolicitudesComponent implements OnInit {
  solicitud?: SolicitudDetalle;
  id?: number;
  esAdmin = false;
  cargando = false;
  mensaje = '';
  error = '';
  sugerencia?: SugerenciaClasificacion;
  resumen?: ResumenSolicitud;
  cargandoIA = false;

  clasificacionForm = { tipo: '', prioridad: '', justificacionPrioridad: '', comentario: '' };
  asignacionForm    = { responsableId: null as number | null, comentario: '' };
  atencionForm      = { observacion: '' };
  cierreForm        = { resolucion: '', observacionCierre: '' };

  tipos       = ['REGISTRO_ASIGNATURA','HOMOLOGACION','CANCELACION_ASIGNATURA','SOLICITUD_CUPO','CONSULTA_ACADEMICA'];
  prioridades = ['BAJA','MEDIA','ALTA'];
  resoluciones= ['APROBADA','RECHAZADA','CANCELADA'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private solicitudService: SolicitudService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.esAdmin = this.authService.esAdmin();
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : undefined;
    console.log('ID de solicitud:', this.id);
    if (this.id && this.id > 0) {
      this.cargar();
    }
  }

  cargar() {
    this.cargando = true;
    this.solicitudService.obtener(this.id!).subscribe({
      next: (data) => {
        this.solicitud = data;
        this.cargando = false;
        this.cdr.detectChanges();
        console.log('Solicitud cargada:', data);
      },
      error: (err) => {
        console.error('Error cargando solicitud:', err);
        this.error = 'No se pudo cargar la solicitud.';
        this.cargando = false;
      }
    });
  }

  clasificar() {
    // Si no hay prioridad manual el backend la calcula con el motor de reglas
    const payload: any = { tipo: this.clasificacionForm.tipo };
    if (this.clasificacionForm.prioridad) payload.prioridad = this.clasificacionForm.prioridad;
    if (this.clasificacionForm.justificacionPrioridad) payload.justificacionPrioridad = this.clasificacionForm.justificacionPrioridad;
    if (this.clasificacionForm.comentario) payload.comentario = this.clasificacionForm.comentario;

    this.solicitudService.clasificar(this.id!, payload).subscribe({
      next: data => { this.solicitud = data; this.ok('Solicitud clasificada. Prioridad: ' + data.prioridad); },
      error: err  => this.err(err)
    });
  }

  asignar() {
    this.solicitudService.asignarResponsable(this.id!, this.asignacionForm).subscribe({
      next: data => { this.solicitud = data; this.ok('Responsable asignado.'); },
      error: err  => this.err(err)
    });
  }

  atender() {
    this.solicitudService.atender(this.id!, this.atencionForm).subscribe({
      next: data => { this.solicitud = data; this.ok('Solicitud marcada como atendida.'); },
      error: err  => this.err(err)
    });
  }

  cerrar() {
    this.solicitudService.cerrar(this.id!, this.cierreForm).subscribe({
      next: data => { this.solicitud = data; this.ok('Solicitud cerrada definitivamente.'); },
      error: err  => this.err(err)
    });
  }

  // ─── IA ──────────────────────────────────────────────────────
  sugerirClasificacion() {
    if (!this.solicitud?.descripcion) return;
    this.cargandoIA = true;
    this.solicitudService.sugerirClasificacion(this.solicitud.descripcion).subscribe({
      next: s => {
        this.sugerencia = s;
        this.clasificacionForm.tipo = s.tipoSugerido;
        this.clasificacionForm.justificacionPrioridad = s.justificacion;
        this.cargandoIA = false;
      },
      error: () => this.cargandoIA = false
    });
  }

  generarResumen() {
    this.cargandoIA = true;
    this.solicitudService.generarResumen(this.id!).subscribe({
      next: r => { this.resumen = r; this.cargandoIA = false; },
      error: () => this.cargandoIA = false
    });
  }

  verHistorial() { this.router.navigate(['/solicitudes', this.id, 'historial']); }
  volver()       { this.router.navigate(['/dashboard']); }

  private ok(msg: string) { this.mensaje = msg; this.error = ''; }
  private err(e: any)     { this.error = e?.error?.mensaje || 'Ocurrió un error.'; this.mensaje = ''; }

  puedeClasificar() { return this.esAdmin && this.solicitud?.estado === 'REGISTRADA'; }
  puedeAsignar()    { return this.esAdmin && this.solicitud?.estado === 'CLASIFICADA'; }
  puedeAtender()    { return this.esAdmin && this.solicitud?.estado === 'EN_ATENCION'; }
  puedeCerrar()     { return this.esAdmin && this.solicitud?.estado === 'ATENDIDA'; }
}
