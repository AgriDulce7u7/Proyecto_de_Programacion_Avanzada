import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  SolicitudDetalle, EventoHistorial,
  SugerenciaClasificacion, ResumenSolicitud
} from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private readonly API = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  registrar(data: any): Observable<SolicitudDetalle> {
    return this.http.post<SolicitudDetalle>(`${this.API}/solicitudes`, data);
  }

  listar(filtros?: any): Observable<SolicitudDetalle[]> {
    let params = new HttpParams();
    if (filtros?.estado)        params = params.set('estado', filtros.estado);
    if (filtros?.tipo)          params = params.set('tipo', filtros.tipo);
    if (filtros?.prioridad)     params = params.set('prioridad', filtros.prioridad);
    if (filtros?.responsableId) params = params.set('responsableId', filtros.responsableId);
    return this.http.get<SolicitudDetalle[]>(`${this.API}/solicitudes`, { params });
  }

  obtener(id: number): Observable<SolicitudDetalle> {
    return this.http.get<SolicitudDetalle>(`${this.API}/solicitudes/${id}`);
  }

  clasificar(id: number, data: any): Observable<SolicitudDetalle> {
    return this.http.patch<SolicitudDetalle>(`${this.API}/solicitudes/${id}/clasificar`, data);
  }

  asignarResponsable(id: number, data: any): Observable<SolicitudDetalle> {
    return this.http.patch<SolicitudDetalle>(`${this.API}/solicitudes/${id}/asignar-responsable`, data);
  }

  atender(id: number, data: any): Observable<SolicitudDetalle> {
    return this.http.patch<SolicitudDetalle>(`${this.API}/solicitudes/${id}/atender`, data);
  }

  cerrar(id: number, data: any): Observable<SolicitudDetalle> {
    return this.http.patch<SolicitudDetalle>(`${this.API}/solicitudes/${id}/cerrar`, data);
  }

  historial(id: number): Observable<EventoHistorial[]> {
    return this.http.get<EventoHistorial[]>(`${this.API}/solicitudes/${id}/historial`);
  }

  sugerirClasificacion(descripcion: string): Observable<SugerenciaClasificacion> {
    return this.http.post<SugerenciaClasificacion>(
      `${this.API}/ia/sugerir-clasificacion`, descripcion,
      { headers: { 'Content-Type': 'text/plain' } }
    );
  }

  generarResumen(id: number): Observable<ResumenSolicitud> {
    return this.http.get<ResumenSolicitud>(`${this.API}/ia/resumen/${id}`);
  }
}
