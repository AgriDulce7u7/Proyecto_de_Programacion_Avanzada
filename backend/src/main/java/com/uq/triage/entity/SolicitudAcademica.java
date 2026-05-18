package com.uq.triage.entity;

import com.uq.triage.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agregado raiz del sistema. Gestiona el ciclo de vida completo
 * de un tramite academico desde REGISTRADA hasta CERRADA.
 */
@Entity
@Table(name = "solicitudes")
@Getter @Setter @NoArgsConstructor
public class SolicitudAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSolicitud tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalOrigen canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.REGISTRADA;

    @Enumerated(EnumType.STRING)
    @Column
    private Prioridad prioridad;

    @Column(name = "justificacion_prioridad", length = 500)
    private String justificacionPrioridad;

    // Fecha limite asociada al tramite (usada por las reglas de priorizacion)
    @Column(name = "fecha_limite")
    private LocalDateTime fechaLimite;

    // Indica si tiene impacto en grado (usado por reglas de priorizacion)
    @Column(name = "impacto_grado")
    private Boolean impactoGrado = false;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "solicitante_id", nullable = false)
    private String solicitanteId;

    @Column(name = "solicitante_nombre", nullable = false)
    private String solicitanteNombre;

    @Column(name = "solicitante_email", nullable = false)
    private String solicitanteEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsable_id")
    private Responsable responsable;

    @Enumerated(EnumType.STRING)
    @Column
    private ResolucionCierre resolucion;

    @Column(name = "observacion_cierre", length = 2000)
    private String observacionCierre;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("fechaAccion ASC")
    private List<HistorialSolicitud> historial = new ArrayList<>();

    @PrePersist
    public void antesDeGuardar() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void antesDeActualizar() {
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }
}
