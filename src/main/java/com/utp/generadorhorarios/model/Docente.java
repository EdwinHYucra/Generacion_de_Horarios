package com.utp.generadorhorarios.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "docente")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(exclude = {"cursos", "disponibilidades"})
@ToString(exclude = {"cursos", "disponibilidades"})
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false)
    private Boolean estado = true;

    // Un docente puede dictar varios cursos
    @ManyToMany
    @JoinTable(
        name = "docente_curso",
        joinColumns = @JoinColumn(name = "docente_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private Set<Curso> cursos;

    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL)
    private Set<DisponibilidadDocente> disponibilidades;
}
