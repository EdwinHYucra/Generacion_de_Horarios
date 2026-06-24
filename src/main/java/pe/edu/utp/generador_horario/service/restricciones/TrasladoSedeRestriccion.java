package pe.edu.utp.generador_horario.service.restricciones;

import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.dao.RestriccionSedeDAO;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Valida el tiempo minimo de traslado cuando un docente cambia de sede.
 *
 * <p>Patron aplicado: Strategy. Esta regla queda aislada para que el generador
 * pueda combinarla con otras restricciones sin conocer su detalle interno.</p>
 */
@Component
public class TrasladoSedeRestriccion implements RestriccionHorario {

    private static final String CODIGO = "TRASLADO_SEDE";

    private final RestriccionSedeDAO restriccionSedeDAO;

    public TrasladoSedeRestriccion(RestriccionSedeDAO restriccionSedeDAO) {
        this.restriccionSedeDAO = restriccionSedeDAO;
    }

    @Override
    public ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales) {

        if (candidata == null || asignacionesActuales == null) {
            return ResultadoRestriccionDTO.valido();
        }

        if (!tieneDatosMinimos(candidata)) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                    "La asignacion candidata no tiene datos suficientes para validar traslado entre sedes.");
        }

        for (AsignacionHorarioCandidataDTO existente : asignacionesActuales) {
            if (!tieneDatosMinimos(existente)) {
                continue;
            }

            // Solo interesa comparar clases del mismo docente, el mismo dia
            // y en sedes distintas. Las demas restricciones se encargan de
            // cruces de aula/docente o disponibilidad.
            if (!esMismoDocenteYDia(candidata, existente) || esMismaSede(candidata, existente)) {
                continue;
            }

            ResultadoRestriccionDTO resultado = validarTrasladoEntre(candidata, existente);
            if (!resultado.isValido()) {
                return resultado;
            }
        }

        return ResultadoRestriccionDTO.valido();
    }

    private boolean tieneDatosMinimos(AsignacionHorarioCandidataDTO asignacion) {
        return asignacion.getIdDocente() != null
                && asignacion.getIdSede() != null
                && asignacion.getDiaSemana() != null
                && asignacion.getHoraInicio() != null
                && asignacion.getHoraFin() != null;
    }

    private boolean esMismoDocenteYDia(
            AsignacionHorarioCandidataDTO candidata,
            AsignacionHorarioCandidataDTO existente) {
        return Objects.equals(candidata.getIdDocente(), existente.getIdDocente())
                && Objects.equals(candidata.getDiaSemana(), existente.getDiaSemana());
    }

    private boolean esMismaSede(
            AsignacionHorarioCandidataDTO candidata,
            AsignacionHorarioCandidataDTO existente) {
        return Objects.equals(candidata.getIdSede(), existente.getIdSede());
    }

    private ResultadoRestriccionDTO validarTrasladoEntre(
            AsignacionHorarioCandidataDTO candidata,
            AsignacionHorarioCandidataDTO existente) {

        // Se valida en ambos sentidos porque la candidata puede quedar antes
        // o despues de la clase ya aceptada.
        if (existente.getHoraFin().compareTo(candidata.getHoraInicio()) <= 0) {
            return validarBrecha(
                    existente.getIdSede(),
                    candidata.getIdSede(),
                    ChronoUnit.MINUTES.between(existente.getHoraFin(), candidata.getHoraInicio()));
        }

        if (candidata.getHoraFin().compareTo(existente.getHoraInicio()) <= 0) {
            return validarBrecha(
                    candidata.getIdSede(),
                    existente.getIdSede(),
                    ChronoUnit.MINUTES.between(candidata.getHoraFin(), existente.getHoraInicio()));
        }

        return ResultadoRestriccionDTO.valido();
    }

    private ResultadoRestriccionDTO validarBrecha(Long sedeOrigen, Long sedeDestino, long minutosDisponibles) {
        Optional<Integer> tiempoMinimo = restriccionSedeDAO.obtenerTiempoMinimo(sedeOrigen, sedeDestino);

        if (tiempoMinimo.isEmpty()) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                    "No existe una regla de traslado configurada entre las sedes seleccionadas.");
        }

        if (minutosDisponibles < tiempoMinimo.get()) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                    "El tiempo entre clases no alcanza para trasladarse entre sedes.");
        }

        return ResultadoRestriccionDTO.valido();
    }
}
