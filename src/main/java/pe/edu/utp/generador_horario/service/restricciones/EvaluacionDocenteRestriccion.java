package pe.edu.utp.generador_horario.service.restricciones;

import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.EvaluacionDocenteDAO;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;
import java.util.Optional;

/**
 * Restringe asignaciones cuando el docente tuvo mala evaluacion en el curso.
 *
 * <p>Patron aplicado: Strategy. La regla se enchufa al validador sin modificar
 * el algoritmo principal. Se usa el ciclo anterior al activo, por lo que un
 * docente nuevo o sin evaluaciones historicas no queda restringido.</p>
 */
@Component
public class EvaluacionDocenteRestriccion implements RestriccionHorario {

    private static final String CODIGO = "EVALUACION_DOCENTE";
    private static final double PUNTAJE_MINIMO_PERMITIDO = 12.0;

    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final EvaluacionDocenteDAO evaluacionDocenteDAO;

    public EvaluacionDocenteRestriccion(
            CicloAcademicoDAO cicloAcademicoDAO,
            EvaluacionDocenteDAO evaluacionDocenteDAO) {
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.evaluacionDocenteDAO = evaluacionDocenteDAO;
    }

    @Override
    public ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales) {

        if (candidata == null || candidata.getIdDocente() == null || candidata.getIdCurso() == null) {
            return ResultadoRestriccionDTO.valido();
        }

        Optional<Long> cicloAnterior = cicloAcademicoDAO.findIdAnteriorAlActivo();
        if (cicloAnterior.isEmpty()) {
            return ResultadoRestriccionDTO.valido();
        }

        Optional<Double> promedio = evaluacionDocenteDAO.obtenerPromedioPuntaje(
                cicloAnterior.get(),
                candidata.getIdDocente(),
                candidata.getIdCurso());

        if (promedio.isPresent() && promedio.get() < PUNTAJE_MINIMO_PERMITIDO) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                    "El docente tuvo evaluacion menor a 6/10 en este curso durante el ciclo anterior.");
        }

        return ResultadoRestriccionDTO.valido();
    }
}
