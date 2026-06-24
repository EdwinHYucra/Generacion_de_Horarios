package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;

public interface ValidadorRestriccionesHorarioService {

    ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales);
}
