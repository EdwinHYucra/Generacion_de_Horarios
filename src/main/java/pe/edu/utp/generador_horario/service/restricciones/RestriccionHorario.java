package pe.edu.utp.generador_horario.service.restricciones;

import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;

/**
 * Strategy para validar una regla del motor de generacion de horarios.
 */
public interface RestriccionHorario {

    /**
     * Evalua si una clase candidata puede agregarse al horario en construccion.
     *
     * @param candidata clase que se intenta ubicar
     * @param asignacionesActuales clases ya aceptadas en la opcion de horario
     * @return resultado de la regla
     */
    ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales);
}
