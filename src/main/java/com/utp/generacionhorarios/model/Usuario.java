package com.utp.generacionhorarios.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa a los usuarios
 * registrados en el sistema.
 *
 * Almacena credenciales de acceso y
 * el rol asignado a cada usuario.
 *
 * @author Dayanna
 */

@Entity
@Table(name = "usuario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    public enum Rol { ADMIN, DOCENTE }
}
