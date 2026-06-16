package pe.edu.utp.generador_horario.entidad;

public class DocenteCurso {
    private Long idDocenteCurso;
    private Long idDocente;
    private Long idCurso;

    public Long getIdDocenteCurso() {
        return idDocenteCurso;
    }

    public void setIdDocenteCurso(Long idDocenteCurso) {
        this.idDocenteCurso = idDocenteCurso;
    }

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
}