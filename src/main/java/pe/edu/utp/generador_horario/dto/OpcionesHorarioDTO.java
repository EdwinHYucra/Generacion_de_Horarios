package pe.edu.utp.generador_horario.dto;

import java.util.List;

public class OpcionesHorarioDTO {

    private Integer opcion;

    private List<HorarioDetalleDTO> bloques;

    private String observacion;

    public Integer getOpcion() {
        return opcion;
    }

    public void setOpcion(Integer opcion) {
        this.opcion = opcion;
    }

    public List<HorarioDetalleDTO> getBloques() {
        return bloques;
    }

    public void setBloques(List<HorarioDetalleDTO> bloques) {
        this.bloques = bloques;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}