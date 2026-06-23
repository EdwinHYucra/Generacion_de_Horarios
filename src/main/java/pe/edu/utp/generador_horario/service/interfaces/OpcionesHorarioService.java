package pe.edu.utp.generador_horario.service.interfaces;

import java.util.List;

import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;

public interface OpcionesHorarioService {

    List<OpcionesHorarioDTO> generarHorarios(Long docenteId);

}
