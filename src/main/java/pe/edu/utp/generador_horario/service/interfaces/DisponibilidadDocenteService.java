package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.dto.BloqueDisponibilidadDTO;
import java.util.List;

public interface DisponibilidadDocenteService {
    List<BloqueDisponibilidadDTO> listarPorEmail(String email);

    void guardarPorEmail(String email, List<BloqueDisponibilidadDTO> bloques);
}