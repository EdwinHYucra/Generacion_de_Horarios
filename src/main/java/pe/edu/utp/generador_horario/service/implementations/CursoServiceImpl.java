package pe.edu.utp.generador_horario.service.implementations;

import pe.edu.utp.generador_horario.entidad.Curso;
import pe.edu.utp.generador_horario.dao.CursoDAO;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementa la logica de negocio para la administracion de cursos.
 *
 * <p>
 * Valida horas semanales y evita duplicados de codigo o nombre antes
 * de guardar cambios en la base de datos.
 * </p>
 *
 * @author Edwin
 */
@Service
public class CursoServiceImpl implements CursoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CursoServiceImpl.class);

    private final CursoDAO cursoDAO;

    public CursoServiceImpl(CursoDAO cursoDAO) {
        this.cursoDAO = cursoDAO;
    }

    @Override
    public List<Curso> listarCursos() {
        return cursoDAO.findByEstadoTrue();
    }

    @Override
    public Curso obtenerPorId(Long id) {
        return cursoDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
    }

    @Override
    public Curso guardarCurso(Curso curso) {
        if (curso.getEstado() == null) {
            curso.setEstado(true);
        }

        if (curso.getHorasSemanales() == null || curso.getHorasSemanales() <= 0) {
            throw new IllegalArgumentException("Las horas semanales deben ser mayores a 0.");
        }

        if (curso.getIdCurso() == null) {
            if (cursoDAO.existsByCodigo(curso.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un curso con ese código.");
            }

            if (cursoDAO.existsByNombre(curso.getNombre())) {
                throw new IllegalArgumentException("Ya existe un curso con ese nombre.");
            }
        } else {
            if (cursoDAO.existsByCodigoAndIdCursoNot(curso.getCodigo(), curso.getIdCurso())) {
                throw new IllegalArgumentException("Ya existe otro curso con ese código.");
            }

            if (cursoDAO.existsByNombreAndIdCursoNot(curso.getNombre(), curso.getIdCurso())) {
                throw new IllegalArgumentException("Ya existe otro curso con ese nombre.");
            }
        }

        try {
            return cursoDAO.save(curso);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar el curso. id={}, codigo={}, nombre={}",
                    curso.getIdCurso(), curso.getCodigo(), curso.getNombre(), e);
            throw e;
        }
    }

    @Override
    public void desactivarCurso(Long id) {
        Curso curso = obtenerPorId(id);
        curso.setEstado(false);
        try {
            cursoDAO.save(curso);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la desactivacion del curso. id={}", id, e);
            throw e;
        }
    }

    @Override
    public List<Curso> listarPorTipo(String tipo) {
        return cursoDAO.findByTipoAndEstadoTrue(tipo);
    }

    @Override
    public List<Curso> listarCursosDeCarrera() {
        return cursoDAO.findCursosDeCarrera();
    }

    @Override
    public List<Curso> listarCursosGenerales() {
        return cursoDAO.findCursosGenerales();
    }

    @Override
    public List<Curso> listarCursosDeCarreraPorCarrera(String carrera) {
        return cursoDAO.findCursosDeCarreraPorCarrera(carrera);
    }

    @Override
    public List<Curso> listarCursosGeneralesPorCarrera(String carrera) {
        return cursoDAO.findCursosGeneralesPorCarrera(carrera);
    }

    @Override
    public List<Curso> listarCursosDeCarreraPorCarreraFiltrandoEvaluacion(
            String carrera,
            Long idDocente,
            Long idCicloAnterior,
            double puntajeMinimo) {
        return cursoDAO.findCursosDeCarreraPorCarreraFiltrandoEvaluacion(
                carrera,
                idDocente,
                idCicloAnterior,
                puntajeMinimo);
    }

    @Override
    public List<Curso> listarCursosGeneralesPorCarreraFiltrandoEvaluacion(
            String carrera,
            Long idDocente,
            Long idCicloAnterior,
            double puntajeMinimo) {
        return cursoDAO.findCursosGeneralesPorCarreraFiltrandoEvaluacion(
                carrera,
                idDocente,
                idCicloAnterior,
                puntajeMinimo);
    }
}
