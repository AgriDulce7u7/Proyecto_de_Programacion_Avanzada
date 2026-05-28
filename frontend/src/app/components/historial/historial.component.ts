import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../services/solicitud.service';
import { EventoHistorial } from '../../models/solicitud.model';

@Component({
  standalone: false,
  selector: 'app-historial',
  templateUrl: './historial.component.html',
  styleUrls: ['./historial.component.css']
})
export class HistorialComponent implements OnInit {
  historial: EventoHistorial[] = [];
  solicitudId?: number;
  cargando = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private solicitudService: SolicitudService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.solicitudId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargando = true;
    this.solicitudService.historial(this.solicitudId!).subscribe({
      next: data => {
        this.historial = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  icono(accion: string): string {
    const m: any = {
      REGISTRO: '📝', CLASIFICACION: '🏷️',
      ASIGNACION_RESPONSABLE: '👤', MARCADA_ATENDIDA: '✅', CIERRE: '🔒'
    };
    return m[accion] || '•';
  }

  volver() { this.router.navigate(['/solicitudes', this.solicitudId]); }
}
