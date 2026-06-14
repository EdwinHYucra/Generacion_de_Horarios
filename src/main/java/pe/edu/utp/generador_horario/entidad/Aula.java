package pe.edu.utp.generador_horario.entidad;

/**
 * Representa un aula fisica o virtual disponible para programar clases.
 *
 * <p>Incluye datos de identificacion, capacidad, tipo, ubicacion, sede y
 * estado para controlar su uso en la generacion de horarios.</p>
 *
 * @author Edwin
 */
public class Aula {
    private Long idAula;
    private String codigo;
    private String nombre;
    private String tipo;
    private Integer capacidad;
    private String ubicacion;
    private Sede sede;
    private Boolean estado = true;

    public Aula() {
    }

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}

