package com.uq.triage.dto;
import com.uq.triage.enums.Prioridad;
import com.uq.triage.enums.TipoSolicitud;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class SugerenciaClasificacionDTO {
    private TipoSolicitud tipoSugerido;
    private Prioridad prioridadSugerida;
    private String justificacion;
    private String advertencia;
}
