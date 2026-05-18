package com.uq.triage.service;

import com.uq.triage.enums.EstadoSolicitud;
import com.uq.triage.exception.TransicionInvalidaException;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Valida que las transiciones de estado sean coherentes con la maquina
 * de estados definida en RF-04. El mapa declarativo es la fuente de
 * verdad: REGISTRADA -> CLASIFICADA -> EN_ATENCION -> ATENDIDA -> CERRADA.
 */
@Service
public class EstadoTransicionService {

    private static final Map<EstadoSolicitud, EstadoSolicitud> TRANSICIONES = Map.of(
        EstadoSolicitud.REGISTRADA,  EstadoSolicitud.CLASIFICADA,
        EstadoSolicitud.CLASIFICADA, EstadoSolicitud.EN_ATENCION,
        EstadoSolicitud.EN_ATENCION, EstadoSolicitud.ATENDIDA,
        EstadoSolicitud.ATENDIDA,    EstadoSolicitud.CERRADA
    );

    public void validar(EstadoSolicitud actual, EstadoSolicitud siguiente) {
        EstadoSolicitud permitido = TRANSICIONES.get(actual);
        if (permitido == null) {
            throw new TransicionInvalidaException("La solicitud esta CERRADA y no puede modificarse.");
        }
        if (!permitido.equals(siguiente)) {
            throw new TransicionInvalidaException(
                "Transicion invalida: no se puede pasar de [" + actual +
                "] a [" + siguiente + "]. El siguiente estado permitido es: [" + permitido + "]."
            );
        }
    }
}
