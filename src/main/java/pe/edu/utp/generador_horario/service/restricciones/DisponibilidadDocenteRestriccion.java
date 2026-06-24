package pe.edu.utp.generador_horario.service.restricciones;

import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;

/**
 * Valida que la clase candidata este cubierta por la disponibilidad del docente.
 *
 * <p>La disponibilidad se registra como rangos por ciclo academico. La regla
 * valida que exista al menos un rango que cubra completamente la clase
 * candidata.</p>
 */
@Component
public class DisponibilidadDocenteRestriccion implements RestriccionHorario {

    private static final String CODIGO = "DISPONIBILIDAD_DOCENTE";
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final DisponibilidadDocenteDAO disponibilidadDocenteDAO;

    public DisponibilidadDocenteRestriccion(
            CicloAcademicoDAO cicloAcademicoDAO,
            DisponibilidadDocenteDAO disponibilidadDocenteDAO) {
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.disponibilidadDocenteDAO = disponibilidadDocenteDAO;
    }

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
                    "La duracion de la clase debe ser positiva.");
        }

        Long cicloActivoId = cicloAcademicoDAO.findIdActivo()
                .orElse(null);

        if (cicloActivoId == null) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                    "No existe un ciclo academico activo para validar disponibilidad.");
        }

        long rangosDisponibles = disponibilidadDocenteDAO.countBloquesDisponiblesEnRango(
                candidata.getIdDocente(),
                cicloActivoId,
                candidata.getDiaSemana(),
                candidata.getHoraInicio(),
                candidata.getHoraFin());

        if (rangosDisponibles == 0) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                "El docente no tiene disponibilidad completa para ese horario.");
        }

        return ResultadoRestriccionDTO.valido();
    }

    private boolean tieneDatosMinimos(AsignacionHorarioCandidataDTO candidata) {
        return candidata.getIdDocente() != null
                && candidata.getDiaSemana() != null
                && candidata.getHoraInicio() != null
                && candidata.getHoraFin() != null;
    }
}
