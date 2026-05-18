package com.uq.triage.repository;

import com.uq.triage.entity.Responsable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResponsableRepository extends JpaRepository<Responsable, Long> {
    List<Responsable> findByActivoTrue();
}
