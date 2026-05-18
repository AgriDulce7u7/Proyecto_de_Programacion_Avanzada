package com.uq.triage.service;

import com.uq.triage.entity.HistorialSolicitud;
import com.uq.triage.entity.SolicitudAcademica;
import com.uq.triage.enums.AccionHistorial;
import com.uq.triage.enums.EstadoSolicitud;
import com.uq.triage.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Gestiona el registro auditable de cada accion sobre una solicitud (RF-06).
 * Cada llamada a registrar() crea una entrada inmutable en el historial.
 */
@Service
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialRepository historialRepository;

    public void registrar(SolicitudAcademica solicitud, AccionHistorial accion,
                          EstadoSolicitud estadoAnterior, EstadoSolicitud estadoNuevo,
                          String usuarioId, String usuarioNombre, String observaciones) {
        HistorialSolicitud entrada = new HistorialSolicitud();
        entrada.setSolicitud(solicitud);
        entrada.setAccion(accion);
        entrada.setEstadoAnterior(estadoAnterior);
        entrada.setEstadoNuevo(estadoNuevo);
        entrada.setUsuarioId(usuarioId);
        entrada.setUsuarioNombre(usuarioNombre);
        entrada.setObservaciones(observaciones);
        historialRepository.save(entrada);
    }
}
