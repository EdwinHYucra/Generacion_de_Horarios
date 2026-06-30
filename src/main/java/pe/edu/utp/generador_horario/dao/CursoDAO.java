package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoDAO {
    List<Curso> findByEstadoTrue();

    List<Curso> findByTipoAndEstadoTrue(String tipo);

    Optional<Curso> findById(Long id);

    Curso save(Curso curso);

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    boolean existsByCodigoAndIdCursoNot(String codigo, Long idCurso);

    boolean existsByNombreAndIdCursoNot(String nombre, Long idCurso);

    List<Curso> findCursosDeCarrera();

    List<Curso> findCursosGenerales();

    List<Curso> findCursosDeCarreraPorCarrera(String carrera);

    List<Curso> findCursosGeneralesPorCarrera(String carrera);

    List<Curso> findCursosDeCarreraPorCarreraFiltrandoEvaluacion(
            String carrera,
            Long idDocente,
            Long idCicloAnterior,
            double puntajeMinimo);

    List<Curso> findCursosGeneralesPorCarreraFiltrandoEvaluacion(
            String carrera,
            Long idDocente,
            Long idCicloAnterior,
            double puntajeMinimo);
}
