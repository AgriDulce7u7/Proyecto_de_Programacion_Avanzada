package com.uq.triage.dto;
import com.uq.triage.enums.AccionHistorial;
import com.uq.triage.enums.EstadoSolicitud;
import lombok.Getter; import lombok.Setter;
import java.time.LocalDateTime;
@Getter @Setter
public class EventoHistorialDTO {
    private Long id;
    private Long solicitudId;
    private AccionHistorial accion;
    private EstadoSolicitud estadoAnterior;
    private EstadoSolicitud estadoNuevo;
    private String usuarioId;
    private String usuarioNombre;
    private String observaciones;
    private LocalDateTime fechaHora;
}
