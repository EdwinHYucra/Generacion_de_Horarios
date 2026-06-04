package com.utp.generacionhorarios.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "docente_curso")
public class DocenteCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_docente_curso")
    private Long idDocenteCurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public DocenteCurso() {
    }

    public Long getIdDocenteCurso() {
        return idDocenteCurso;
    }

    public Docente getDocente() {
        return docente;
    }

    public Curso getCurso() {
        return curso;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setIdDocenteCurso(Long idDocenteCurso) {
        this.idDocenteCurso = idDocenteCurso;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}