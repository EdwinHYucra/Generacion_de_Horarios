package pe.edu.utp.generador_horario.dto;

import java.util.List;

public class DisponibilidadRequestDTO {
    private List<BloqueDisponibilidadDTO> bloques;

    public List<BloqueDisponibilidadDTO> getBloques() {
        return bloques;
    }

    public void setBloques(List<BloqueDisponibilidadDTO> bloques) {
        this.bloques = bloques;
    }
}