package com.utp.generacionhorarios.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "carrera_curso",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_carrera", "id_curso"})
        }
)
public class CarreraCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera_curso")
    private Long idCarreraCurso;

    @ManyToOne
    @JoinColumn(name = "id_carrera", nullable = false)
    private Carrera carrera;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @Column(name = "ciclo", nullable = false)
    private Integer ciclo;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    public CarreraCurso() {
    }

    public Long getIdCarreraCurso() {
        return idCarreraCurso;
    }

    public void setIdCarreraCurso(Long idCarreraCurso) {
        this.idCarreraCurso = idCarreraCurso;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}