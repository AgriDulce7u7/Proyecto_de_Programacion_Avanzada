package com.uq.triage.entity;

import com.uq.triage.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad base del sistema con herencia JOINED.
 * Genera la tabla 'usuarios'. Subclases: Solicitante, Responsable.
 * El campo 'password' se almacena con BCrypt para la autenticacion JWT.
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, unique = true)
    private String identificacion;

    @Column(nullable = false)
    private boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Agregado en Hito 3 para autenticacion JWT
    @Column(name = "pwd")
    private String password;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}