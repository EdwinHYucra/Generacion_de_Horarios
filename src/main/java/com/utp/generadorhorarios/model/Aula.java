package com.utp.generadorhorarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aula")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo; // Ej: "A-201"

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAula tipo;

    private Integer piso;

    @Column(nullable = false)
    private Boolean estado = true;

    public enum TipoAula { TEORICA, LABORATORIO, TALLER }
}
