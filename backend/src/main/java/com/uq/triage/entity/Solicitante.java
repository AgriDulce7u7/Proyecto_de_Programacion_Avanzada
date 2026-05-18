package com.uq.triage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitantes")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter @NoArgsConstructor
public class Solicitante extends Usuario {
    @Column(name = "codigo_estudiantil", unique = true)
    private String codigoEstudiantil;
    @Column private String programa;
    @Column private int semestre;
}
