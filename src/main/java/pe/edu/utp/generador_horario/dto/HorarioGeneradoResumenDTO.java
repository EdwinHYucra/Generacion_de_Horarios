package pe.edu.utp.generador_horario.dto;

/**
 * Resumen de una opcion de horario persistida para revision administrativa.
 */
public class HorarioGeneradoResumenDTO {

    private Long idHorario;
    private Long idDocente;
    private String docente;
    private Integer opcion;
    private String estado;
    private String fechaGeneracion;
    private Integer totalBloques;

    public Long getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Long idHorario) {
        this.idHorario = idHorario;
    }

    public Long getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Long idDocente) {
        this.idDocente = idDocente;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public Integer getOpcion() {
        return opcion;
    }

    public void setOpcion(Integer opcion) {
        this.opcion = opcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Integer getTotalBloques() {
        return totalBloques;
    }

    public void setTotalBloques(Integer totalBloques) {
        this.totalBloques = totalBloques;
    }
}
