package com.uq.triage.dto;

import com.uq.triage.enums.CanalOrigen;
import com.uq.triage.enums.TipoSolicitud;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO de entrada para registrar una solicitud (RF-01).
 * Incluye fechaLimite e impactoGrado que el motor de reglas
 * usa para calcular la prioridad automaticamente (RF-03).
 */
@Getter @Setter
public class SolicitudCrearDTO {
    @NotNull(message = "El tipo es obligatorio")
    private TipoSolicitud tipo;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(min = 10, max = 2000)
    private String descripcion;

    @NotNull(message = "El canal es obligatorio")
    private CanalOrigen canal;

    @NotBlank(message = "El ID del solicitante es obligatorio")
    private String solicitanteId;

    @NotBlank(message = "El nombre del solicitante es obligatorio")
    private String solicitanteNombre;

    @NotBlank @Email
    private String solicitanteEmail;

    // Campos opcionales usados por el motor de priorizacion
    private LocalDateTime fechaLimite;
    private boolean impactoGrado = false;
}
