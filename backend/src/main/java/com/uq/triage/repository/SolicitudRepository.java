package com.uq.triage.repository;

import com.uq.triage.entity.SolicitudAcademica;
import com.uq.triage.enums.EstadoSolicitud;
import com.uq.triage.enums.Prioridad;
import com.uq.triage.enums.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudAcademica, Long> {

    @Query("""
        SELECT s FROM SolicitudAcademica s
        WHERE (:estado IS NULL OR s.estado = :estado)
          AND (:tipo IS NULL OR s.tipo = :tipo)
          AND (:prioridad IS NULL OR s.prioridad = :prioridad)
          AND (:responsableId IS NULL OR s.responsable.id = :responsableId)
        ORDER BY s.fechaRegistro DESC
    """)
    List<SolicitudAcademica> buscarConFiltros(
            @Param("estado") EstadoSolicitud estado,
            @Param("tipo") TipoSolicitud tipo,
            @Param("prioridad") Prioridad prioridad,
            @Param("responsableId") Long responsableId
    );
}
