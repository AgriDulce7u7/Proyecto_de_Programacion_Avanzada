package com.uq.triage.repository;

import com.uq.triage.entity.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<HistorialSolicitud, Long> {
    List<HistorialSolicitud> findBySolicitudIdOrderByFechaAccionAsc(Long solicitudId);
}
