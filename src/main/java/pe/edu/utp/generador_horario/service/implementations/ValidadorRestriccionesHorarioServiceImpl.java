package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;
import pe.edu.utp.generador_horario.service.interfaces.ValidadorRestriccionesHorarioService;
import pe.edu.utp.generador_horario.service.restricciones.RestriccionHorario;

import java.util.List;

/**
 * Ejecuta las restricciones del generador como una cadena de validacion.
 *
 * <p>Patron aplicado: Chain of Responsibility apoyado en Spring DI. Cada
 * restriccion decide si la candidata puede continuar o debe rechazarse.</p>
 */
@Service
public class ValidadorRestriccionesHorarioServiceImpl implements ValidadorRestriccionesHorarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorRestriccionesHorarioServiceImpl.class);

    private final List<RestriccionHorario> restricciones;

    public ValidadorRestriccionesHorarioServiceImpl(List<RestriccionHorario> restricciones) {
        this.restricciones = restricciones;
    }

    @Override
    public ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales) {

        // Chain of Responsibility: cada restriccion decide si la candidata
        // puede continuar. La primera que falle detiene el proceso.
        for (RestriccionHorario restriccion : restricciones) {
            ResultadoRestriccionDTO resultado = restriccion.validar(candidata, asignacionesActuales);
            if (!resultado.isValido()) {
                LOGGER.debug("Asignacion rechazada por restriccion. codigo={}, mensaje={}",
                        resultado.getCodigo(), resultado.getMensaje());
                return resultado;
            }
        }

        return ResultadoRestriccionDTO.valido();
    }
}
