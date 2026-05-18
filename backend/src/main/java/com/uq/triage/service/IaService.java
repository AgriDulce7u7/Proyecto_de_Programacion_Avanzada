package com.uq.triage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uq.triage.dto.ResumenSolicitudDTO;
import com.uq.triage.dto.SugerenciaClasificacionDTO;
import com.uq.triage.entity.HistorialSolicitud;
import com.uq.triage.entity.SolicitudAcademica;
import com.uq.triage.enums.Prioridad;
import com.uq.triage.enums.TipoSolicitud;
import com.uq.triage.exception.SolicitudNotFoundException;
import com.uq.triage.repository.HistorialRepository;
import com.uq.triage.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Servicio de integracion con la API de Anthropic (Claude).
 *
 * RF-09: Genera un resumen textual del historial de una solicitud.
 * RF-10: Sugiere tipo y prioridad a partir de la descripcion del tramite.
 * RF-11: Si la API falla, el sistema retorna respuestas basicas sin caerse.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IaService {

    private final SolicitudRepository solicitudRepository;
    private final HistorialRepository historialRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.api.model}")
    private String modelo;

    // ─── RF-09: Resumen del historial ─────────────────────────────
    public ResumenSolicitudDTO generarResumen(Long solicitudId) {
        SolicitudAcademica solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));
        List<HistorialSolicitud> historial =
                historialRepository.findBySolicitudIdOrderByFechaAccionAsc(solicitudId);

        ResumenSolicitudDTO resultado = new ResumenSolicitudDTO();
        resultado.setSolicitudId(solicitudId);
        resultado.setEstadoActual(solicitud.getEstado().name());
        resultado.setTotalAcciones(historial.size());

        try {
            resultado.setResumen(llamarAnthropic(construirPromptResumen(solicitud, historial)));
        } catch (Exception e) {
            // RF-11: fallback sin IA
            log.warn("API IA no disponible para resumen: {}", e.getMessage());
            resultado.setResumen(resumenBasico(solicitud, historial));
        }
        return resultado;
    }

    // ─── RF-10: Sugerencia de clasificacion ───────────────────────
    public SugerenciaClasificacionDTO sugerirClasificacion(String descripcion) {
        SugerenciaClasificacionDTO resultado = new SugerenciaClasificacionDTO();
        try {
            String json = llamarAnthropic(construirPromptSugerencia(descripcion));
            parsearSugerencia(json, resultado);
        } catch (Exception e) {
            // RF-11: fallback sin IA
            log.warn("API IA no disponible para sugerencia: {}", e.getMessage());
            resultado.setTipoSugerido(TipoSolicitud.CONSULTA_ACADEMICA);
            resultado.setPrioridadSugerida(Prioridad.MEDIA);
            resultado.setJustificacion("Clasificacion automatica no disponible. Por favor clasifique manualmente.");
            resultado.setAdvertencia("El servicio de IA no esta disponible temporalmente.");
        }
        return resultado;
    }

    // ─── Construccion de prompts ──────────────────────────────────
    private String construirPromptResumen(SolicitudAcademica s, List<HistorialSolicitud> h) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un asistente del sistema de triage academico de la Universidad del Quindio. ");
        sb.append("Resume en maximo 3 oraciones claras el siguiente tramite:\n\n");
        sb.append("Tipo: ").append(s.getTipo()).append("\n");
        sb.append("Estado: ").append(s.getEstado()).append("\n");
        sb.append("Prioridad: ").append(s.getPrioridad() != null ? s.getPrioridad() : "No asignada").append("\n");
        sb.append("Descripcion: ").append(s.getDescripcion()).append("\n");
        sb.append("Historial (").append(h.size()).append(" acciones):\n");
        h.forEach(e -> sb.append("- ").append(e.getAccion())
                .append(": ").append(e.getObservaciones()).append("\n"));
        return sb.toString();
    }

    private String construirPromptSugerencia(String descripcion) {
        return "Eres un asistente del sistema de triage academico de la Universidad del Quindio. "
               + "Analiza la descripcion y responde UNICAMENTE con JSON valido, sin texto adicional:\n\n"
               + "Descripcion: " + descripcion + "\n\n"
               + "Responde exactamente con:\n"
               + "{\"tipo\": \"<REGISTRO_ASIGNATURA|HOMOLOGACION|CANCELACION_ASIGNATURA|SOLICITUD_CUPO|CONSULTA_ACADEMICA>\","
               + "\"prioridad\": \"<BAJA|MEDIA|ALTA>\","
               + "\"justificacion\": \"<explicacion breve>\"}";
    }

    // ─── Llamada HTTP a Anthropic ─────────────────────────────────
    private String llamarAnthropic(String prompt) {
        Map<String, Object> body = Map.of(
                "model", modelo,
                "max_tokens", 500,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );
        String respuesta = webClientBuilder.build()
                .post().uri(apiUrl)
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve().bodyToMono(String.class).block();
        try {
            return objectMapper.readTree(respuesta)
                    .path("content").get(0).path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error parseando respuesta Anthropic: " + e.getMessage());
        }
    }

    private void parsearSugerencia(String json, SugerenciaClasificacionDTO dto) {
        try {
            String limpio = json.replaceAll("```json", "").replaceAll("```", "").trim();
            JsonNode node = objectMapper.readTree(limpio);
            dto.setTipoSugerido(TipoSolicitud.valueOf(node.path("tipo").asText("CONSULTA_ACADEMICA")));
            dto.setPrioridadSugerida(Prioridad.valueOf(node.path("prioridad").asText("MEDIA")));
            dto.setJustificacion(node.path("justificacion").asText(""));
            dto.setAdvertencia("Sugerencia generada por IA. Debe ser confirmada o ajustada por un usuario humano.");
        } catch (Exception e) {
            throw new RuntimeException("Error parseando JSON de IA: " + e.getMessage());
        }
    }

    private String resumenBasico(SolicitudAcademica s, List<HistorialSolicitud> h) {
        String ultima = h.isEmpty() ? "ninguna" : h.get(h.size() - 1).getAccion().name();
        return String.format("Solicitud de tipo %s en estado %s con prioridad %s. "
                + "Se han registrado %d acciones. Ultima accion: %s.",
                s.getTipo(), s.getEstado(),
                s.getPrioridad() != null ? s.getPrioridad() : "no asignada",
                h.size(), ultima);
    }
}
