package com.utp.generacionhorarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitud_cambio")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "detalle_id", nullable = false)
    private DetalleHorario detalle;

    @ManyToOne
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "nuevo_dia")
    private DisponibilidadDocente.DiaSemana nuevoDia;

    @ManyToOne
    @JoinColumn(name = "nuevo_bloque_id")
    private BloqueHorario nuevoBloque;

    @ManyToOne
    @JoinColumn(name = "nuevo_aula_id")
    private Aula nuevaAula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCambio estado = EstadoCambio.PENDIENTE;

    public enum EstadoCambio { PENDIENTE, APROBADO, RECHAZADO }
}
