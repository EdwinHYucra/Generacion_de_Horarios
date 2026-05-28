package com.utp.generadorhorarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "disponibilidad_docente",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"docente_id", "semestre_id", "dia_semana", "bloque_id"}
    )
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DisponibilidadDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @ManyToOne
    @JoinColumn(name = "bloque_id", nullable = false)
    private BloqueHorario bloque;

    public enum DiaSemana {
        LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO
    }
}
