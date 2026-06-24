package pe.edu.utp.generador_horario.entidad;

import java.time.LocalTime;

public class DisponibilidadDocente {
    private Long idDisponibilidad;
    private Long idCicloAcademico;
    private Long idDocente;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean estado = true;

    public Long getIdDisponibilidad() {
        return idDisponibilidad;
    }

    public void setIdDisponibilidad(Long idDisponibilidad) {
        this.idDisponibilidad = idDisponibilidad;
    }

    public Long getIdCicloAcademico() {
        return idCicloAcademico;
    }

    public void setIdCicloAcademico(Long idCicloAcademico) {
        this.idCicloAcademico = idCicloAcademico;
    }

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
