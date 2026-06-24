package pe.edu.utp.generador_horario.entidad;

/**
 * Regla administrativa para definir minutos minimos de traslado entre sedes.
 *
 * <p>El algoritmo consulta esta configuracion cuando un docente tiene clases
 * consecutivas en sedes distintas.</p>
 */
public class RestriccionSede {

    private Long idRestriccion;
    private Sede sedeOrigen = new Sede();
    private Sede sedeDestino = new Sede();
    private Integer tiempoMinimoMinutos;

    public Long getIdRestriccion() {
        return idRestriccion;
    }

    public void setIdRestriccion(Long idRestriccion) {
        this.idRestriccion = idRestriccion;
    }

    public Sede getSedeOrigen() {
        return sedeOrigen;
    }

    public void setSedeOrigen(Sede sedeOrigen) {
        this.sedeOrigen = sedeOrigen;
    }

    public Sede getSedeDestino() {
        return sedeDestino;
    }

    public void setSedeDestino(Sede sedeDestino) {
        this.sedeDestino = sedeDestino;
    }

    public Integer getTiempoMinimoMinutos() {
        return tiempoMinimoMinutos;
    }

    public void setTiempoMinimoMinutos(Integer tiempoMinimoMinutos) {
        this.tiempoMinimoMinutos = tiempoMinimoMinutos;
    }
}
