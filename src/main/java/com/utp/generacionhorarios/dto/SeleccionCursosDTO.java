package com.utp.generacionhorarios.dto;

import java.util.ArrayList;
import java.util.List;

public class SeleccionCursosDTO {

    private Long docenteId;
    private List<Long> cursosSeleccionados = new ArrayList<>();

    public SeleccionCursosDTO() {
    }

    public SeleccionCursosDTO(Long docenteId, List<Long> cursosSeleccionados) {
        this.docenteId = docenteId;
        this.cursosSeleccionados = cursosSeleccionados;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public List<Long> getCursosSeleccionados() {
        return cursosSeleccionados;
    }

    public void setCursosSeleccionados(List<Long> cursosSeleccionados) {
        this.cursosSeleccionados = cursosSeleccionados;
    }
}