package com.utp.generacionhorarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_horario",
    uniqueConstraints = {
        // Un docente no puede tener dos clases al mismo tiempo (CU-06)
        @UniqueConstraint(columnNames = {"horario_id", "docente_id", "dia_semana", "bloque_id"}),
        // Un aula no puede tener dos cursos al mismo tiempo (CU-06)
        @UniqueConstraint(columnNames = {"horario_id", "aula_id", "dia_semana", "bloque_id"})
    }
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DisponibilidadDocente.DiaSemana diaSemana;

    @ManyToOne
    @JoinColumn(name = "bloque_id", nullable = false)
    private BloqueHorario bloque;
}
