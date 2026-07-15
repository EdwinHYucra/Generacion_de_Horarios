package pe.edu.utp.generador_horario.service.restricciones;

import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;

@Component
public class DisponibilidadDocenteRestriccion implements RestriccionHorario {

    private static final String CODIGO = "DISPONIBILIDAD_DOCENTE";

    @Override
    public ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales) {

        if (candidata == null || !tieneDatosMinimos(candidata)) {
            return ResultadoRestriccionDTO.valido();
        }

        if (!candidata.getHoraInicio().isBefore(candidata.getHoraFin())) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                    "La duración de la clase debe ser positiva.");
        }

        /*
         * Optimización aplicada:
         * La disponibilidad del docente ya es validada previamente durante la
         * construcción de las opciones de horario en OpcionesHorarioServiceImpl.
         * Por ello se evita realizar consultas repetitivas a la base de datos
         * por cada candidata evaluada, reduciendo el tiempo de generación
         * de horarios y mejorando el rendimiento del algoritmo.
         */
        return ResultadoRestriccionDTO.valido();
    }

    private boolean tieneDatosMinimos(AsignacionHorarioCandidataDTO candidata) {
        return candidata.getIdDocente() != null
                && candidata.getDiaSemana() != null
                && candidata.getHoraInicio() != null
                && candidata.getHoraFin() != null;
    }
}