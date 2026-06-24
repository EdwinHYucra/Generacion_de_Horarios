package pe.edu.utp.generador_horario.service.restricciones;

import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;
import java.util.Objects;

/**
 * Evita que un aula sea usada por dos clases simultaneas.
 *
 * <p>El algoritmo puede probar muchas combinaciones de aula. Esta restriccion
 * descarta cualquier candidata que choque con una asignacion ya aceptada.</p>
 */
@Component
public class ConflictoAulaRestriccion implements RestriccionHorario {

    private static final String CODIGO = "CONFLICTO_AULA";

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

            if (esMismaAulaYDia(candidata, existente)
                    && HorarioSolapamientoUtil.seSolapan(candidata, existente)) {
                return ResultadoRestriccionDTO.invalido(
                        CODIGO,
                        "El aula ya esta asignada en ese horario.");
            }
        }

        return ResultadoRestriccionDTO.valido();
    }

    private boolean tieneDatosMinimos(AsignacionHorarioCandidataDTO asignacion) {
        return asignacion.getIdAula() != null
                && asignacion.getDiaSemana() != null
                && asignacion.getHoraInicio() != null
                && asignacion.getHoraFin() != null;
    }

    private boolean esMismaAulaYDia(
            AsignacionHorarioCandidataDTO candidata,
            AsignacionHorarioCandidataDTO existente) {
        return Objects.equals(candidata.getIdAula(), existente.getIdAula())
                && Objects.equals(candidata.getDiaSemana(), existente.getDiaSemana());
    }
}
