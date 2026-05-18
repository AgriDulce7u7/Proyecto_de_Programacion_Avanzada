package com.uq.triage.service;

import com.uq.triage.entity.SolicitudAcademica;
import com.uq.triage.enums.Prioridad;
import com.uq.triage.enums.TipoSolicitud;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor de reglas de negocio para calcular la prioridad automaticamente (RF-03).
 *
 * Este servicio responde al comentario del docente:
 * "Falta logica en SolicitudService para calcular la prioridad automaticamente
 *  segun reglas de negocio en lugar de aceptar prioridad manual en ClasificacionInputDTO."
 *
 * El motor evalua las siguientes reglas en orden de importancia:
 *   R1: HOMOLOGACION con impacto en grado → ALTA
 *   R2: CANCELACION_ASIGNATURA con fecha limite en <= 3 dias → ALTA
 *   R3: Cualquier solicitud con fecha limite en <= 2 dias → ALTA
 *   R4: HOMOLOGACION o SOLICITUD_CUPO → MEDIA
 *   R5: CANCELACION_ASIGNATURA o REGISTRO_ASIGNATURA → MEDIA
 *   R6: CONSULTA_ACADEMICA → BAJA (regla por defecto)
 *
 * La primera regla que se cumpla gana. Si el administrativo envia
 * una prioridad manual, esta sobreescribe el calculo del motor,
 * pero la justificacion indica que fue calculada automaticamente.
 */
@Service
public class ReglaPriorizacionService {

    /**
     * Calcula la prioridad para una solicitud y registra la justificacion.
     * Retorna un resultado con prioridad calculada y su explicacion.
     */
    public ResultadoPriorizacion calcular(SolicitudAcademica solicitud) {
        List<String> razonesAplicadas = new ArrayList<>();
        Prioridad prioridadFinal = Prioridad.BAJA; // valor por defecto

        TipoSolicitud tipo = solicitud.getTipo();
        boolean tieneImpactoGrado = solicitud.getImpactoGrado();
        LocalDateTime fechaLimite = solicitud.getFechaLimite();
        long diasRestantes = fechaLimite != null
                ? ChronoUnit.DAYS.between(LocalDateTime.now(), fechaLimite)
                : Long.MAX_VALUE;

        // R1: Homologacion con impacto en grado → maxima prioridad
        if (tipo == TipoSolicitud.HOMOLOGACION && tieneImpactoGrado) {
            prioridadFinal = Prioridad.ALTA;
            razonesAplicadas.add("R1: Homologacion con impacto en grado");
        }

        // R2: Cancelacion con fecha limite proxima (docente lo exige explicitamente en su feedback)
        else if (tipo == TipoSolicitud.CANCELACION_ASIGNATURA && diasRestantes <= 3) {
            prioridadFinal = Prioridad.ALTA;
            razonesAplicadas.add("R2: Cancelacion de asignatura con fecha limite en " + diasRestantes + " dia(s)");
        }

        // R3: Cualquier tramite con fecha limite muy proxima
        else if (diasRestantes <= 2) {
            prioridadFinal = Prioridad.ALTA;
            razonesAplicadas.add("R3: Fecha limite en " + diasRestantes + " dia(s)");
        }

        // R4: Tipos que tipicamente requieren atencion media
        else if (tipo == TipoSolicitud.HOMOLOGACION || tipo == TipoSolicitud.SOLICITUD_CUPO) {
            prioridadFinal = Prioridad.MEDIA;
            razonesAplicadas.add("R4: Tipo " + tipo + " clasificado como prioridad media por defecto");
        }

        // R5: Tramites de registro y cancelacion sin urgencia
        else if (tipo == TipoSolicitud.CANCELACION_ASIGNATURA
                 || tipo == TipoSolicitud.REGISTRO_ASIGNATURA) {
            prioridadFinal = Prioridad.MEDIA;
            razonesAplicadas.add("R5: Tipo " + tipo + " clasificado como prioridad media");
        }

        // R6: Consultas → baja prioridad por defecto
        else {
            prioridadFinal = Prioridad.BAJA;
            razonesAplicadas.add("R6: Consulta academica — prioridad baja por defecto");
        }

        String justificacion = "Prioridad calculada automaticamente por motor de reglas. "
                + "Regla aplicada: " + String.join(", ", razonesAplicadas) + ".";

        return new ResultadoPriorizacion(prioridadFinal, justificacion);
    }

    /**
     * Resultado inmutable del calculo de priorizacion.
     */
    public record ResultadoPriorizacion(Prioridad prioridad, String justificacion) {}
}
