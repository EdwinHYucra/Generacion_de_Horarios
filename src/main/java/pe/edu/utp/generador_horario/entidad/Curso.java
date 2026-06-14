package pe.edu.utp.generador_horario.entidad;

/**
 * Representa un curso academico que puede ser asignado a docentes,
 * carreras y horarios.
 *
 * <p>Registra codigo, nombre, horas semanales, tipo y estado del curso.</p>
 *
 * @author Edwin
 */
public class Curso {
    private Long idCurso;
    private String codigo;
    private String nombre;
    private Integer horasSemanales;
    private String tipo;
    private Boolean estado = true;

    public Curso() {
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
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

    public Integer getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(Integer horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}

