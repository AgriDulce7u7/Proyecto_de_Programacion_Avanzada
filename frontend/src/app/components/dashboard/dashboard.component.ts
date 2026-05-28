import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SolicitudService } from '../../services/solicitud.service';
import { AuthService } from '../../services/auth.service';
import { SolicitudDetalle } from '../../models/solicitud.model';

@Component({
  standalone: false,
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  solicitudes: SolicitudDetalle[] = [];
  cargando = false;
  filtroEstado = '';
  filtroTipo = '';
  filtroPrioridad = '';
  nombre = '';
  esAdmin = false;

  estados    = ['REGISTRADA','CLASIFICADA','EN_ATENCION','ATENDIDA','CERRADA'];
  tipos      = ['REGISTRO_ASIGNATURA','HOMOLOGACION','CANCELACION_ASIGNATURA','SOLICITUD_CUPO','CONSULTA_ACADEMICA'];
  prioridades= ['BAJA','MEDIA','ALTA'];

  constructor(
    private solicitudService: SolicitudService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.nombre  = this.authService.getNombre() || '';
    this.esAdmin = this.authService.esAdmin();
    this.cargar();
  }

  cargar() {
    this.cargando = true;
    const filtros: any = {};
    if (this.filtroEstado)    filtros.estado    = this.filtroEstado;
    if (this.filtroTipo)      filtros.tipo       = this.filtroTipo;
    if (this.filtroPrioridad) filtros.prioridad  = this.filtroPrioridad;
    this.solicitudService.listar(filtros).subscribe({
      next: data => { this.solicitudes = data; this.cargando = false; },
      error: ()   => this.cargando = false
    });
  }

  limpiarFiltros() {
    this.filtroEstado = '';
    this.filtroTipo = '';
    this.filtroPrioridad = '';
    this.cargar();
  }

  verDetalle(id: number) { this.router.navigate(['/solicitudes', id]); }
  nueva()                { this.router.navigate(['/solicitudes/nueva']); }
  logout()               { this.authService.logout(); }

  badgeColor(estado: string): string {
    const c: any = { REGISTRADA:'#6c757d', CLASIFICADA:'#0d6efd',
                     EN_ATENCION:'#fd7e14', ATENDIDA:'#198754', CERRADA:'#343a40' };
    return c[estado] || '#6c757d';
  }

  prioridadColor(p: string): string {
    return p === 'ALTA' ? '#dc3545' : p === 'MEDIA' ? '#fd7e14' : '#198754';
  }
}
