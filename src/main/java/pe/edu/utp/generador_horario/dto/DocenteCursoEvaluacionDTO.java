package pe.edu.utp.generador_horario.dto;

/**
 * Opcion publica de evaluacion formada por docente, curso y ciclo anterior.
 */
public class DocenteCursoEvaluacionDTO {

    private Long idDocente;
    private String nombreDocente;
    private Long idCurso;
    private String nombreCurso;
    private String cicloAcademico;

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public String getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(String cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }
}
