package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.Curso;

import java.util.List;

/**
 * Define las operaciones disponibles para administrar cursos.
 *
 * @author Edwin
 */
public interface CursoService {

    /**
     * Lista los cursos activos.
     *
     * @return cursos activos
     */
    List<Curso> listarCursos();

    /**
     * Obtiene un curso por identificador.
     *
     * @param id identificador del curso
     * @return curso encontrado
     */
    Curso obtenerPorId(Long id);

    /**
     * Crea o actualiza un curso.
     *
     * @param curso curso a guardar
     * @return curso persistido
     */
    Curso guardarCurso(Curso curso);

    /**
     * Desactiva logicamente un curso.
     *
     * @param id identificador del curso
     */
    void desactivarCurso(Long id);

    List<Curso> listarPorTipo(String tipo);
}
