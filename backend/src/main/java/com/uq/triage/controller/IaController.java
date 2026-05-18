package com.uq.triage.controller;

import com.uq.triage.dto.ResumenSolicitudDTO;
import com.uq.triage.dto.SugerenciaClasificacionDTO;
import com.uq.triage.service.IaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de asistencia con IA. Requieren rol ADMINISTRATIVO.
 * RF-09: Resumen del historial de una solicitud.
 * RF-10: Sugerencia de tipo y prioridad desde la descripcion.
 * RF-11: Si la IA falla, retorna respuesta basica sin caerse.
 */
@RestController
@RequestMapping("/api/v1/ia")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class IaController {

    private final IaService iaService;

    @GetMapping("/resumen/{solicitudId}")
    public ResponseEntity<ResumenSolicitudDTO> resumen(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(iaService.generarResumen(solicitudId));
    }

    @PostMapping("/sugerir-clasificacion")
    public ResponseEntity<SugerenciaClasificacionDTO> sugerir(@RequestBody String descripcion) {
        return ResponseEntity.ok(iaService.sugerirClasificacion(descripcion));
    }
}
