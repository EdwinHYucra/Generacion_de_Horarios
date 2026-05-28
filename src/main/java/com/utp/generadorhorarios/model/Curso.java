package com.utp.generadorhorarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curso")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 15)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Integer creditos;

    @Column(name = "horas_teoria", nullable = false)
    private Integer horasTeoria = 0;

    @Column(name = "horas_practica", nullable = false)
    private Integer horasPractica = 0;

    private Integer ciclo;

    @Column(nullable = false)
    private Boolean estado = true;
}
