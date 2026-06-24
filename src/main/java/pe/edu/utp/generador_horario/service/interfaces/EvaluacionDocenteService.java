package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.dto.DocenteCursoEvaluacionDTO;
import pe.edu.utp.generador_horario.dto.EvaluacionDocenteRequestDTO;

import java.util.List;

public interface EvaluacionDocenteService {

    List<DocenteCursoEvaluacionDTO> listarOpcionesEvaluables();

    void guardarEvaluacion(EvaluacionDocenteRequestDTO request);
}
