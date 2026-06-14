package pe.edu.utp.generador_horario.entidad;

/**
 * Representa la relacion entre una carrera y un curso.
 *
 * <p>Permite indicar en que ciclo del plan de estudios se dicta un curso y
 * si la asignacion se encuentra activa.</p>
 *
 * @author Edwin
 */
public class CarreraCurso {
    private Long idCarreraCurso;
    private Carrera carrera;
    private Curso curso;
    private Integer ciclo;
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

