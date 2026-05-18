package com.uq.triage.entity;

import com.uq.triage.enums.AccionHistorial;
import com.uq.triage.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Registro inmutable de cada accion sobre una solicitud (RF-06).
 * Una vez persistido, ningun campo debe modificarse.
 */
@Entity
@Table(name = "historial_solicitud")
@Getter @Setter @NoArgsConstructor
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudAcademica solicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccionHistorial accion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoSolicitud estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo")
    private EstadoSolicitud estadoNuevo;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Column(name = "usuario_nombre", nullable = false)
    private String usuarioNombre;

    @Column(length = 1000)
    private String observaciones;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;

    @PrePersist
    public void antesDeGuardar() {
        this.fechaAccion = LocalDateTime.now();
    }
}
