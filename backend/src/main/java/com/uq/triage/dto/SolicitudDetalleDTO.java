package com.uq.triage.dto;
import com.uq.triage.enums.*;
import lombok.Getter; import lombok.Setter;
import java.time.LocalDateTime;
@Getter @Setter
public class SolicitudDetalleDTO {
    private Long id;
    private TipoSolicitud tipo;
    private EstadoSolicitud estado;
    private Prioridad prioridad;
    private String justificacionPrioridad;
    private CanalOrigen canal;
    private String descripcion;
    private boolean impactoGrado;
    private LocalDateTime fechaLimite;
    private String solicitanteId;
    private String solicitanteNombre;
    private String solicitanteEmail;
    private Long responsableId;
    private String responsableNombre;
    private ResolucionCierre resolucion;
    private String observacionCierre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaCierre;
}
