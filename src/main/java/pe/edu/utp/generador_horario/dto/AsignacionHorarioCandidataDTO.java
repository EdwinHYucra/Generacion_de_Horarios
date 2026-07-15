package pe.edu.utp.generador_horario.dto;

import java.time.LocalTime;

/**
 * Representa una clase candidata que el generador intenta ubicar.
 *
 * <p>Este DTO no se persiste directamente. Funciona como unidad de trabajo del
 * algoritmo: cada candidata se compara contra las asignaciones ya aceptadas y
 * contra las reglas de negocio disponibles.</p>
 */
public class AsignacionHorarioCandidataDTO {

    private Long idDocente;
    private Long idCurso;
    private Long idAula;
    private Long idSede;
    private String tipoCurso;
    private String tipoAula;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponibilidadPrevalidada;

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public Long getIdSede() {
        return idSede;
    }

    public void setIdSede(Long idSede) {
        this.idSede = idSede;
    }

    public String getTipoCurso() {
        return tipoCurso;
    }

    public void setTipoCurso(String tipoCurso) {
        this.tipoCurso = tipoCurso;
    }

    public String getTipoAula() {
        return tipoAula;
    }

    public void setTipoAula(String tipoAula) {
        this.tipoAula = tipoAula;
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

    public boolean isDisponibilidadPrevalidada() {
        return disponibilidadPrevalidada;
    }

    public void setDisponibilidadPrevalidada(boolean disponibilidadPrevalidada) {
        this.disponibilidadPrevalidada = disponibilidadPrevalidada;
    }
}
