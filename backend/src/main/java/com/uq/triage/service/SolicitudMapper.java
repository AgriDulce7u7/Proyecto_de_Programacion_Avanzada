package com.uq.triage.service;

import com.uq.triage.dto.EventoHistorialDTO;
import com.uq.triage.dto.SolicitudDetalleDTO;
import com.uq.triage.entity.HistorialSolicitud;
import com.uq.triage.entity.SolicitudAcademica;
import org.springframework.stereotype.Component;

/**
 * Convierte entidades JPA a DTOs de respuesta.
 * Esta separacion evita exponer la estructura interna al cliente.
 */
@Component
public class SolicitudMapper {

    public SolicitudDetalleDTO toDetalleDTO(SolicitudAcademica s) {
        SolicitudDetalleDTO dto = new SolicitudDetalleDTO();
        dto.setId(s.getId());
        dto.setTipo(s.getTipo());
        dto.setEstado(s.getEstado());
        dto.setPrioridad(s.getPrioridad());
        dto.setJustificacionPrioridad(s.getJustificacionPrioridad());
        dto.setCanal(s.getCanal());
        dto.setDescripcion(s.getDescripcion());
        dto.setImpactoGrado(s.getImpactoGrado());
        dto.setFechaLimite(s.getFechaLimite());
        dto.setSolicitanteId(s.getSolicitanteId());
        dto.setSolicitanteNombre(s.getSolicitanteNombre());
        dto.setSolicitanteEmail(s.getSolicitanteEmail());
        dto.setResolucion(s.getResolucion());
        dto.setObservacionCierre(s.getObservacionCierre());
        dto.setFechaCreacion(s.getFechaRegistro());
        dto.setFechaActualizacion(s.getFechaUltimaActualizacion());
        dto.setFechaCierre(s.getFechaCierre());
        if (s.getResponsable() != null) {
            dto.setResponsableId(s.getResponsable().getId());
            dto.setResponsableNombre(s.getResponsable().getNombreCompleto());
        }
        return dto;
    }

    public EventoHistorialDTO toEventoDTO(HistorialSolicitud h) {
        EventoHistorialDTO dto = new EventoHistorialDTO();
        dto.setId(h.getId());
        dto.setSolicitudId(h.getSolicitud().getId());
        dto.setAccion(h.getAccion());
        dto.setEstadoAnterior(h.getEstadoAnterior());
        dto.setEstadoNuevo(h.getEstadoNuevo());
        dto.setUsuarioId(h.getUsuarioId());
        dto.setUsuarioNombre(h.getUsuarioNombre());
        dto.setObservaciones(h.getObservaciones());
        dto.setFechaHora(h.getFechaAccion());
        return dto;
    }
}
