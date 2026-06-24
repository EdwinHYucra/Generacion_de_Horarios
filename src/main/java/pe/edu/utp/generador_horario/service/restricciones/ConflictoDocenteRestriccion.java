package pe.edu.utp.generador_horario.service.restricciones;

import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;
import java.util.Objects;

/**
 * Evita que un docente sea asignado a dos clases al mismo tiempo.
 *
 * <p>Patron aplicado: Strategy. Esta regla se ejecuta como parte de la cadena
 * de restricciones del generador.</p>
 */
@Component
public class ConflictoDocenteRestriccion implements RestriccionHorario {

    private static final String CODIGO = "CONFLICTO_DOCENTE";

    @Override
    public ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales) {

        if (candidata == null || asignacionesActuales == null || !tieneDatosMinimos(candidata)) {
            return ResultadoRestriccionDTO.valido();
        }

        for (AsignacionHorarioCandidataDTO existente : asignacionesActuales) {
            if (!tieneDatosMinimos(existente)) {
                continue;
            }

            if (esMismoDocenteYDia(candidata, existente)
                    && HorarioSolapamientoUtil.seSolapan(candidata, existente)) {
                return ResultadoRestriccionDTO.invalido(
                        CODIGO,
                        "El docente ya tiene una clase asignada en ese horario.");
            }
        }

        return ResultadoRestriccionDTO.valido();
    }

    private boolean tieneDatosMinimos(AsignacionHorarioCandidataDTO asignacion) {
        return asignacion.getIdDocente() != null
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
}
