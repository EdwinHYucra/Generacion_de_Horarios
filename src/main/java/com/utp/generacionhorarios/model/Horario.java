package com.utp.generacionhorarios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "horario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "detalles")
@EqualsAndHashCode(exclude = "detalles")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHorario estado = EstadoHorario.BORRADOR;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "horario", cascade = CascadeType.ALL)
    private List<DetalleHorario> detalles;

    public enum EstadoHorario { BORRADOR, GENERADO, APROBADO }
}
