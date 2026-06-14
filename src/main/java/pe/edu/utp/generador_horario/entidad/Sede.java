package pe.edu.utp.generador_horario.entidad;

/**
 * Representa una sede institucional donde se administran aulas y recursos.
 *
 * <p>Registra codigo, nombre, direccion y estado para su uso en la
 * organizacion academica.</p>
 *
 * @author Edwin
 */
public class Sede {
    private Long idSede;
    private String codigo;
    private String nombre;
    private String direccion;
    private Boolean estado = true;

    public Sede() {
    }

    public Long getIdSede() {
        return idSede;
    }

    public void setIdSede(Long idSede) {
        this.idSede = idSede;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}

