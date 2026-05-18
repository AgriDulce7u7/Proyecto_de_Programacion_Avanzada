package com.uq.triage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "responsables")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter @NoArgsConstructor
public class Responsable extends Usuario {
    @Column private String cargo;
    @Column private String dependencia;
}
