package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.dto.DocenteCursoEvaluacionDTO;

import java.util.List;
import java.util.Optional;

/**
 * DAO para la encuesta publica de evaluacion docente.
 */
public interface EvaluacionDocenteDAO {

    List<DocenteCursoEvaluacionDTO> listarDocentesCursosEvaluables(Long idCicloAcademico);

    boolean existeDocenteCursoEnCiclo(Long idCicloAcademico, Long idDocente, Long idCurso);

    /**
     * Calcula el promedio de evaluacion de un docente para un curso en un ciclo.
     *
     * @param idCicloAcademico ciclo evaluado
     * @param idDocente docente evaluado
     * @param idCurso curso dictado
     * @return promedio registrado, si existen evaluaciones
     */
    Optional<Double> obtenerPromedioPuntaje(Long idCicloAcademico, Long idDocente, Long idCurso);

    void guardar(Long idCicloAcademico, Long idDocente, Long idCurso, Integer puntaje, String categoria,
            String comentario);
}
