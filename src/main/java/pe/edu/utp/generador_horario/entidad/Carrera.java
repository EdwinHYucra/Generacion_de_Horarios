package pe.edu.utp.generador_horario.entidad;

/**
 * Representa una carrera academica gestionada por el sistema.
 *
 * <p>Su codigo y nombre permiten organizar cursos y asignaciones por plan
 * de estudios.</p>
 *
 * @author Edwin
 */
public class Carrera {
    private Long idCarrera;
    private String codigo;
    private String nombre;
    private Boolean estado = true;

    public Carrera() {
    }

    public Long getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Long idCarrera) {
        this.idCarrera = idCarrera;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}

