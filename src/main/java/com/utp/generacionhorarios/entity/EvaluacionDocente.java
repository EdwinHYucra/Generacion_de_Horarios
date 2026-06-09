package com.utp.generacionhorarios.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "evaluacion_docente")
public class EvaluacionDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "calificacion_promedio", nullable = false)
    private Double calificacionPromedio;

    @Column(name = "periodo", nullable = false, length = 20)
    private String periodo;

    public static final double NOTA_MINIMA = 6.0;

    public EvaluacionDocente() {}

    public Long getIdEvaluacion() { return idEvaluacion; }
    public void setIdEvaluacion(Long id) { this.idEvaluacion = id; }
    public Docente getDocente() { return docente; }
    public void setDocente(Docente docente) { this.docente = docente; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public Double getCalificacionPromedio() { return calificacionPromedio; }
    public void setCalificacionPromedio(Double c) { this.calificacionPromedio = c; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
}