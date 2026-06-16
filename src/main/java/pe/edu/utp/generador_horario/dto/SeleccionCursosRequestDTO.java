package pe.edu.utp.generador_horario.dto;

import java.util.List;

public class SeleccionCursosRequestDTO {
    private List<Long> cursosSeleccionados;

    public List<Long> getCursosSeleccionados() {
        return cursosSeleccionados;
    }

    public void setCursosSeleccionados(List<Long> cursosSeleccionados) {
        this.cursosSeleccionados = cursosSeleccionados;
    }
}