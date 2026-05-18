package com.uq.triage.dto;

import com.uq.triage.enums.Prioridad;
import com.uq.triage.enums.TipoSolicitud;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para clasificar una solicitud (RF-02, RF-03).
 * La prioridad es OPCIONAL: si no se envia, el motor de reglas
 * la calcula automaticamente segun el tipo, fechaLimite e impactoGrado.
 * Si se envia, sobreescribe el calculo del motor (decision humana).
 */
@Getter @Setter
public class ClasificacionInputDTO {
    @NotNull(message = "El tipo es obligatorio")
    private TipoSolicitud tipo;

    // Opcional — el motor de reglas calcula la prioridad si no se envia
    private Prioridad prioridad;

    @Size(max = 500)
    private String justificacionPrioridad;

    @Size(max = 1000)
    private String comentario;
}
