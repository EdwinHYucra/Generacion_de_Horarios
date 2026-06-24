package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.EvaluacionDocenteDAO;
import pe.edu.utp.generador_horario.dto.DocenteCursoEvaluacionDTO;
import pe.edu.utp.generador_horario.dto.EvaluacionDocenteRequestDTO;
import pe.edu.utp.generador_horario.service.interfaces.EvaluacionDocenteService;

import java.util.List;

@Service
public class EvaluacionDocenteServiceImpl implements EvaluacionDocenteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluacionDocenteServiceImpl.class);

    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final EvaluacionDocenteDAO evaluacionDocenteDAO;

    public EvaluacionDocenteServiceImpl(
            CicloAcademicoDAO cicloAcademicoDAO,
            EvaluacionDocenteDAO evaluacionDocenteDAO) {
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.evaluacionDocenteDAO = evaluacionDocenteDAO;
    }

    @Override
    public List<DocenteCursoEvaluacionDTO> listarOpcionesEvaluables() {
        Long cicloAnteriorId = cicloAcademicoDAO.findIdAnteriorAlActivo()
                .orElse(null);

        if (cicloAnteriorId == null) {
            return List.of();
        }

        return evaluacionDocenteDAO.listarDocentesCursosEvaluables(cicloAnteriorId);
    }

    @Override
    public void guardarEvaluacion(EvaluacionDocenteRequestDTO request) {
        Long cicloAnteriorId = obtenerCicloAnteriorId();
        validarRequest(request, cicloAnteriorId);

        String categoria = clasificarPuntaje(request.getPuntaje());
        evaluacionDocenteDAO.guardar(
                cicloAnteriorId,
                request.getIdDocente(),
                request.getIdCurso(),
                request.getPuntaje(),
                categoria,
                request.getComentario());

        LOGGER.info("Evaluacion docente registrada. cicloId={}, docenteId={}, cursoId={}, puntaje={}, categoria={}",
                cicloAnteriorId, request.getIdDocente(), request.getIdCurso(), request.getPuntaje(), categoria);
    }

    private void validarRequest(EvaluacionDocenteRequestDTO request, Long cicloAnteriorId) {
        if (request.getIdDocente() == null || request.getIdCurso() == null) {
            throw new IllegalArgumentException("Seleccione un docente y curso para evaluar.");
        }

        if (request.getPuntaje() == null || request.getPuntaje() < 1 || request.getPuntaje() > 20) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 20.");
        }

        boolean existeRelacion = evaluacionDocenteDAO.existeDocenteCursoEnCiclo(
                cicloAnteriorId,
                request.getIdDocente(),
                request.getIdCurso());

        if (!existeRelacion) {
            throw new IllegalArgumentException(
                    "Solo se puede evaluar a docentes que dictaron ese curso en el ciclo anterior.");
        }
    }

    private Long obtenerCicloAnteriorId() {
        return cicloAcademicoDAO.findIdAnteriorAlActivo()
                .orElseThrow(() -> new IllegalStateException("No existe un ciclo anterior al ciclo activo."));
    }

    private String clasificarPuntaje(Integer puntaje) {
        if (puntaje <= 15) {
            return "MALO";
        }
        if (puntaje <= 18) {
            return "NEUTRAL";
        }
        return "POSITIVO";
    }
}
