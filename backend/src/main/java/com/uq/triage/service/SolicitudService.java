package com.uq.triage.service;

import com.uq.triage.dto.*;
import com.uq.triage.entity.Responsable;
import com.uq.triage.entity.SolicitudAcademica;
import com.uq.triage.enums.AccionHistorial;
import com.uq.triage.enums.EstadoSolicitud;
import com.uq.triage.enums.Prioridad;
import com.uq.triage.exception.*;
import com.uq.triage.repository.*;
import com.uq.triage.service.ReglaPriorizacionService.ResultadoPriorizacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio principal que orquesta la logica de negocio del sistema de triage.
 *
 * Responde al feedback del docente integrando ReglaPriorizacionService
 * para calcular la prioridad automaticamente en lugar de aceptarla
 * como entrada manual sin validacion. El flujo del metodo clasificar()
 * ahora es:
 *   1. Validar transicion de estado
 *   2. Calcular prioridad con el motor de reglas
 *   3. Si el usuario envio una prioridad manual, usarla (decision humana)
 *      pero documentar que fue sobreescrita
 *   4. Guardar y registrar en historial
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ResponsableRepository responsableRepository;
    private final HistorialRepository historialRepository;
    private final EstadoTransicionService transicionService;
    private final HistorialService historialService;
    private final SolicitudMapper mapper;
    private final ReglaPriorizacionService reglaPriorizacion; // feedback docente

    // ─── RF-01: Registrar ─────────────────────────────────────────
    public SolicitudDetalleDTO registrar(SolicitudCrearDTO dto) {
        SolicitudAcademica s = new SolicitudAcademica();
        s.setTipo(dto.getTipo());
        s.setDescripcion(dto.getDescripcion());
        s.setCanal(dto.getCanal());
        s.setSolicitanteId(dto.getSolicitanteId());
        s.setSolicitanteNombre(dto.getSolicitanteNombre());
        s.setSolicitanteEmail(dto.getSolicitanteEmail());
        s.setEstado(EstadoSolicitud.REGISTRADA);
        s.setFechaLimite(dto.getFechaLimite());
        s.setImpactoGrado(dto.isImpactoGrado());

        SolicitudAcademica guardada = solicitudRepository.save(s);
        historialService.registrar(guardada, AccionHistorial.REGISTRO, null,
                EstadoSolicitud.REGISTRADA, dto.getSolicitanteId(),
                dto.getSolicitanteNombre(), "Solicitud registrada por canal " + dto.getCanal());
        return mapper.toDetalleDTO(guardada);
    }

    // ─── RF-07: Listar con filtros ────────────────────────────────
    @Transactional(readOnly = true)
    public List<SolicitudDetalleDTO> listar(com.uq.triage.enums.EstadoSolicitud estado,
            com.uq.triage.enums.TipoSolicitud tipo,
            com.uq.triage.enums.Prioridad prioridad, Long responsableId) {
        return solicitudRepository.buscarConFiltros(estado, tipo, prioridad, responsableId)
                .stream().map(mapper::toDetalleDTO).collect(Collectors.toList());
    }

    // ─── RF-07: Obtener por ID ────────────────────────────────────
    @Transactional(readOnly = true)
    public SolicitudDetalleDTO obtenerPorId(Long id) {
        return mapper.toDetalleDTO(buscarOLanzar(id));
    }

    // ─── RF-02 + RF-03: Clasificar con motor de reglas ────────────
    public SolicitudDetalleDTO clasificar(Long id, ClasificacionInputDTO dto) {
        SolicitudAcademica s = buscarOLanzar(id);
        EstadoSolicitud anterior = s.getEstado();

        // 1. Validar transicion de estado
        transicionService.validar(anterior, EstadoSolicitud.CLASIFICADA);

        // 2. Actualizar tipo (puede cambiar durante la clasificacion)
        s.setTipo(dto.getTipo());

        // 3. Calcular prioridad con el motor de reglas de negocio
        ResultadoPriorizacion resultado = reglaPriorizacion.calcular(s);
        Prioridad prioridadFinal = resultado.prioridad();
        String justificacion = resultado.justificacion();

        // 4. Si el administrativo envio prioridad manual, sobreescribir el calculo
        //    pero documentar la decision para trazabilidad
        if (dto.getPrioridad() != null) {
            prioridadFinal = dto.getPrioridad();
            justificacion = "Prioridad asignada manualmente por administrativo ("
                    + dto.getPrioridad() + "). Motor sugeria: " + resultado.prioridad() + ". "
                    + (dto.getJustificacionPrioridad() != null ? dto.getJustificacionPrioridad() : "");
        } else if (dto.getJustificacionPrioridad() != null) {
            justificacion = resultado.justificacion() + " Observacion: " + dto.getJustificacionPrioridad();
        }

        s.setPrioridad(prioridadFinal);
        s.setJustificacionPrioridad(justificacion);
        s.setEstado(EstadoSolicitud.CLASIFICADA);

        SolicitudAcademica guardada = solicitudRepository.save(s);
        historialService.registrar(guardada, AccionHistorial.CLASIFICACION, anterior,
                EstadoSolicitud.CLASIFICADA, "SISTEMA", "Administrativo",
                "Tipo: " + dto.getTipo() + " | Prioridad calculada: " + prioridadFinal);
        return mapper.toDetalleDTO(guardada);
    }

    // ─── RF-05: Asignar responsable ───────────────────────────────
    public SolicitudDetalleDTO asignarResponsable(Long id, AsignacionInputDTO dto) {
        SolicitudAcademica s = buscarOLanzar(id);
        EstadoSolicitud anterior = s.getEstado();

        // Verificar responsable ANTES de la transicion para retornar 400 correcto
        Responsable responsable = responsableRepository.findById(dto.getResponsableId())
                .orElseThrow(() -> new ResponsableNotFoundException(dto.getResponsableId()));
        if (!responsable.isActivo()) throw new ResponsableInactivoException(dto.getResponsableId());

        transicionService.validar(anterior, EstadoSolicitud.EN_ATENCION);
        s.setResponsable(responsable);
        s.setEstado(EstadoSolicitud.EN_ATENCION);

        SolicitudAcademica guardada = solicitudRepository.save(s);
        historialService.registrar(guardada, AccionHistorial.ASIGNACION_RESPONSABLE,
                anterior, EstadoSolicitud.EN_ATENCION,
                String.valueOf(responsable.getId()), responsable.getNombreCompleto(),
                dto.getComentario() != null ? dto.getComentario()
                        : "Asignado a " + responsable.getNombreCompleto());
        return mapper.toDetalleDTO(guardada);
    }

    // ─── RF-04: Atender ───────────────────────────────────────────
    public SolicitudDetalleDTO atender(Long id, AtencionInputDTO dto) {
        SolicitudAcademica s = buscarOLanzar(id);
        EstadoSolicitud anterior = s.getEstado();
        transicionService.validar(anterior, EstadoSolicitud.ATENDIDA);
        s.setEstado(EstadoSolicitud.ATENDIDA);

        SolicitudAcademica guardada = solicitudRepository.save(s);
        String uid = guardada.getResponsable() != null
                ? String.valueOf(guardada.getResponsable().getId()) : "SISTEMA";
        String unombre = guardada.getResponsable() != null
                ? guardada.getResponsable().getNombreCompleto() : "Sistema";
        historialService.registrar(guardada, AccionHistorial.MARCADA_ATENDIDA,
                anterior, EstadoSolicitud.ATENDIDA, uid, unombre, dto.getObservacion());
        return mapper.toDetalleDTO(guardada);
    }

    // ─── RF-08: Cerrar ────────────────────────────────────────────
    public SolicitudDetalleDTO cerrar(Long id, CierreInputDTO dto) {
        SolicitudAcademica s = buscarOLanzar(id);
        EstadoSolicitud anterior = s.getEstado();
        transicionService.validar(anterior, EstadoSolicitud.CERRADA);
        s.setEstado(EstadoSolicitud.CERRADA);
        s.setResolucion(dto.getResolucion());
        s.setObservacionCierre(dto.getObservacionCierre());
        s.setFechaCierre(LocalDateTime.now());

        SolicitudAcademica guardada = solicitudRepository.save(s);
        historialService.registrar(guardada, AccionHistorial.CIERRE, anterior,
                EstadoSolicitud.CERRADA, "SISTEMA", "Administrativo",
                "Resolucion: " + dto.getResolucion() + " — " + dto.getObservacionCierre());
        return mapper.toDetalleDTO(guardada);
    }

    // ─── RF-06: Historial ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<EventoHistorialDTO> obtenerHistorial(Long id) {
        buscarOLanzar(id);
        return historialRepository.findBySolicitudIdOrderByFechaAccionAsc(id)
                .stream().map(mapper::toEventoDTO).collect(Collectors.toList());
    }

    private SolicitudAcademica buscarOLanzar(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new SolicitudNotFoundException(id));
    }
}
