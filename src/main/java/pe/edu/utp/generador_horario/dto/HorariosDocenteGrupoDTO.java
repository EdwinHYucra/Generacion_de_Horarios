package pe.edu.utp.generador_horario.dto;

/**
 * Agrupa las opciones generadas de un docente para la vista administrativa.
 */
public class HorariosDocenteGrupoDTO {

    private Long idDocente;
    private String docente;
    private Integer cantidadOpciones;
    private Integer totalBloques;
    private String estadoResumen;
    private String fechaGeneracion;

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

    public Integer getCantidadOpciones() {
        return cantidadOpciones;
    }

    public void setCantidadOpciones(Integer cantidadOpciones) {
        this.cantidadOpciones = cantidadOpciones;
    }

    public Integer getTotalBloques() {
        return totalBloques;
    }

    public void setTotalBloques(Integer totalBloques) {
        this.totalBloques = totalBloques;
    }

    public String getEstadoResumen() {
        return estadoResumen;
    }

    public void setEstadoResumen(String estadoResumen) {
        this.estadoResumen = estadoResumen;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
}
