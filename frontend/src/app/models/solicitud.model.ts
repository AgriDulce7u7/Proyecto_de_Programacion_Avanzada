export interface SolicitudDetalle {
  id: number;
  tipo: string;
  estado: string;
  prioridad: string;
  justificacionPrioridad: string;
  canal: string;
  descripcion: string;
  impactoGrado: boolean;
  fechaLimite?: string;
  solicitanteId: string;
  solicitanteNombre: string;
  solicitanteEmail: string;
  responsableId?: number;
  responsableNombre?: string;
  resolucion?: string;
  observacionCierre?: string;
  fechaCreacion: string;
  fechaActualizacion: string;
  fechaCierre?: string;
}

export interface EventoHistorial {
  id: number;
  solicitudId: number;
  accion: string;
  estadoAnterior: string;
  estadoNuevo: string;
  usuarioId: string;
  usuarioNombre: string;
  observaciones: string;
  fechaHora: string;
}

export interface SugerenciaClasificacion {
  tipoSugerido: string;
  prioridadSugerida: string;
  justificacion: string;
  advertencia: string;
}

export interface ResumenSolicitud {
  solicitudId: number;
  resumen: string;
  estadoActual: string;
  totalAcciones: number;
}

export interface LoginResponse {
  token: string;
  correo: string;
  nombre: string;
  rol: string;
  id: number;
}
