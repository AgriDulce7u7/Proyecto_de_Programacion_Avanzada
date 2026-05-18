package com.uq.triage.dto;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class ResumenSolicitudDTO {
    private Long solicitudId;
    private String resumen;
    private String estadoActual;
    private int totalAcciones;
}
