package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.entidad.Curso;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneracionAsyncService;
import pe.edu.utp.generador_horario.service.interfaces.SeleccionCursosAsyncService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SeleccionCursosAsyncServiceImpl implements SeleccionCursosAsyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleccionCursosAsyncServiceImpl.class);
    private static final int MAX_HORAS_SEMANALES = 40;
    private static final double PUNTAJE_MINIMO_CURSO_DISPONIBLE = 7.0;

    private final TaskExecutor taskExecutor;
    private final CursoService cursoService;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final DocenteDAO docenteDAO;
    private final DocenteCursoDAO docenteCursoDAO;
    private final HorarioGeneracionAsyncService horarioGeneracionAsyncService;
    private final Set<Long> docentesEnProceso = ConcurrentHashMap.newKeySet();

    public SeleccionCursosAsyncServiceImpl(
            @Qualifier("horarioTaskExecutor") TaskExecutor taskExecutor,
            CursoService cursoService,
            CicloAcademicoDAO cicloAcademicoDAO,
            DocenteDAO docenteDAO,
            DocenteCursoDAO docenteCursoDAO,
            HorarioGeneracionAsyncService horarioGeneracionAsyncService) {
        this.taskExecutor = taskExecutor;
        this.cursoService = cursoService;
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.docenteDAO = docenteDAO;
        this.docenteCursoDAO = docenteCursoDAO;
        this.horarioGeneracionAsyncService = horarioGeneracionAsyncService;
    }

    @Override
    public boolean programarConfirmacion(Long idDocente, List<Long> cursosSeleccionados) {
        if (idDocente == null) {
            return false;
        }

        if (!docentesEnProceso.add(idDocente)) {
            return false;
        }

        List<Long> cursos = cursosSeleccionados == null ? List.of() : List.copyOf(cursosSeleccionados);
        taskExecutor.execute(() -> confirmarCursos(idDocente, cursos));
        return true;
    }

    @Override
    public boolean estaEnProceso(Long idDocente) {
        return idDocente != null && docentesEnProceso.contains(idDocente);
    }

    private void confirmarCursos(Long idDocente, List<Long> cursosRequest) {
        try {
            Docente docente = docenteDAO.findById(idDocente)
                    .orElseThrow(() -> new IllegalStateException("Docente no encontrado: " + idDocente));
            Long cicloActivoId = obtenerCicloActivoId();

            if (!docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(idDocente, cicloActivoId).isEmpty()) {
                LOGGER.info("Confirmacion de cursos omitida: ya existe seleccion. docenteId={}, cicloId={}",
                        idDocente, cicloActivoId);
                return;
            }

            Set<Long> cursosSeleccionados = new LinkedHashSet<>(cursosRequest);
            List<Curso> cursosGeneralesDisponibles = listarCursosGeneralesDisponibles(
                    obtenerPalabraCarrera(docente),
                    idDocente,
                    cicloAcademicoDAO.findIdAnteriorAlActivo().orElse(null));

            int horasSeleccionadas = calcularHoras(cursosSeleccionados);
            if (horasSeleccionadas > MAX_HORAS_SEMANALES) {
                LOGGER.warn("Confirmacion de cursos rechazada por exceso de carga. docenteId={}, horas={}",
                        idDocente, horasSeleccionadas);
                return;
            }

            int generalesAgregados = completarCursosGeneralesSiNoSelecciono(
                    cursosSeleccionados,
                    cursosGeneralesDisponibles,
                    horasSeleccionadas);

            docenteCursoDAO.deleteByDocenteIdAndCicloId(idDocente, cicloActivoId);

            for (Long idCurso : cursosSeleccionados) {
                docenteCursoDAO.save(idDocente, idCurso, cicloActivoId);
            }

            LOGGER.info("Cursos confirmados en segundo plano. docenteId={}, cicloId={}, cursos={}, generalesAutomaticos={}",
                    idDocente, cicloActivoId, cursosSeleccionados.size(), generalesAgregados);

            horarioGeneracionAsyncService.programarGeneracion(idDocente);
        } catch (RuntimeException ex) {
            LOGGER.error("No se pudo confirmar cursos en segundo plano. docenteId={}", idDocente, ex);
        } finally {
            docentesEnProceso.remove(idDocente);
        }
    }

    private Long obtenerCicloActivoId() {
        return cicloAcademicoDAO.findIdActivo()
                .orElseThrow(() -> new IllegalStateException("No existe un ciclo academico activo."));
    }

    private String obtenerPalabraCarrera(Docente docente) {
        String carreraDocente = docente.getCarrera();
        if (carreraDocente != null && carreraDocente.toLowerCase().contains("sistemas")) {
            return "Sistemas";
        }
        if (carreraDocente != null && carreraDocente.toLowerCase().contains("civil")) {
            return "Civil";
        }
        return carreraDocente;
    }

    private List<Curso> listarCursosGeneralesDisponibles(
            String palabraCarrera,
            Long idDocente,
            Long cicloAnteriorId) {
        return cicloAnteriorId == null
                ? cursoService.listarCursosGeneralesPorCarrera(palabraCarrera)
                : cursoService.listarCursosGeneralesPorCarreraFiltrandoEvaluacion(
                        palabraCarrera,
                        idDocente,
                        cicloAnteriorId,
                        PUNTAJE_MINIMO_CURSO_DISPONIBLE);
    }

    private int completarCursosGeneralesSiNoSelecciono(
            Set<Long> cursosSeleccionados,
            List<Curso> cursosGeneralesDisponibles,
            int horasSeleccionadas) {
        boolean tieneCursoGeneral = cursosGeneralesDisponibles.stream()
                .map(Curso::getIdCurso)
                .anyMatch(cursosSeleccionados::contains);
        if (tieneCursoGeneral || cursosGeneralesDisponibles.isEmpty()) {
            return 0;
        }

        int horasActuales = horasSeleccionadas;
        int agregados = 0;
        List<Curso> candidatos = new ArrayList<>(cursosGeneralesDisponibles);
        Collections.shuffle(candidatos);

        for (Curso curso : candidatos) {
            int horasCurso = horasCurso(curso);
            if (horasCurso <= 0 || cursosSeleccionados.contains(curso.getIdCurso())) {
                continue;
            }
            if (horasActuales + horasCurso <= MAX_HORAS_SEMANALES) {
                cursosSeleccionados.add(curso.getIdCurso());
                return 1;
            }
        }
        return agregados;
    }

    private int calcularHoras(Set<Long> cursosSeleccionados) {
        return cursosSeleccionados.stream()
                .map(cursoService::obtenerPorId)
                .mapToInt(this::horasCurso)
                .sum();
    }

    private int horasCurso(Curso curso) {
        return curso.getHorasSemanales() == null ? 0 : curso.getHorasSemanales();
    }
}
