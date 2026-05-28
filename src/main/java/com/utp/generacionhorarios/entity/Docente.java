package com.utp.generacionhorarios.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo_usuario")
    private String codigoUsuario;

    @Column(name = "contrasenia")
    private String contrasenia;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "codigo_docente")
    private String codigoDocente;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "grado_academico")
    private String gradoAcademico;

    @Column(name = "tipo_contrato")
    private String tipoContrato;

    public Docente() {
    }

    public Long getId() {
        return id;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoDocente() {
        return codigoDocente;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getGradoAcademico() {
        return gradoAcademico;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigoDocente(String codigoDocente) {
        this.codigoDocente = codigoDocente;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public void setGradoAcademico(String gradoAcademico) {
        this.gradoAcademico = gradoAcademico;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
}