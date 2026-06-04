package com.utp.generacionhorarios.dto;

import java.util.ArrayList;
import java.util.List;

public class DisponibilidadDocenteDTO {

    private Long docenteId;
    private List<BloqueDisponibilidadDTO> bloques = new ArrayList<>();

    public DisponibilidadDocenteDTO() {
    }

    public DisponibilidadDocenteDTO(Long docenteId, List<BloqueDisponibilidadDTO> bloques) {
        this.docenteId = docenteId;
        this.bloques = bloques;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public List<BloqueDisponibilidadDTO> getBloques() {
        return bloques;
    }

    public void setBloques(List<BloqueDisponibilidadDTO> bloques) {
        this.bloques = bloques;
    }
}