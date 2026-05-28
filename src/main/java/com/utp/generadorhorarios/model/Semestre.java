package com.utp.generadorhorarios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "semestre")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String nombre; // Ej: "2026-1"

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSemestre estado = EstadoSemestre.ACTIVO;

    public enum EstadoSemestre { ACTIVO, CERRADO }
}
