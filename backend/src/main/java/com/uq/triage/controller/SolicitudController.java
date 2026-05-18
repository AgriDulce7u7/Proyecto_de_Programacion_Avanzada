package com.uq.triage.controller;

import com.uq.triage.dto.*;
import com.uq.triage.enums.EstadoSolicitud;
import com.uq.triage.enums.Prioridad;
import com.uq.triage.enums.TipoSolicitud;
import com.uq.triage.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    public ResponseEntity<SolicitudDetalleDTO> registrar(@Valid @RequestBody SolicitudCrearDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudDetalleDTO>> listar(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Long responsableId) {
        return ResponseEntity.ok(solicitudService.listar(estado, tipo, prioridad, responsableId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDetalleDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/clasificar")
    public ResponseEntity<SolicitudDetalleDTO> clasificar(
            @PathVariable Long id, @Valid @RequestBody ClasificacionInputDTO dto) {
        return ResponseEntity.ok(solicitudService.clasificar(id, dto));
    }

    @PatchMapping("/{id}/asignar-responsable")
    public ResponseEntity<SolicitudDetalleDTO> asignarResponsable(
            @PathVariable Long id, @Valid @RequestBody AsignacionInputDTO dto) {
        return ResponseEntity.ok(solicitudService.asignarResponsable(id, dto));
    }

    @PatchMapping("/{id}/atender")
    public ResponseEntity<SolicitudDetalleDTO> atender(
            @PathVariable Long id, @Valid @RequestBody AtencionInputDTO dto) {
        return ResponseEntity.ok(solicitudService.atender(id, dto));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<SolicitudDetalleDTO> cerrar(
            @PathVariable Long id, @Valid @RequestBody CierreInputDTO dto) {
        return ResponseEntity.ok(solicitudService.cerrar(id, dto));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EventoHistorialDTO>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerHistorial(id));
    }
}
