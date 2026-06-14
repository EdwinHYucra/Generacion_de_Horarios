package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.CarreraCurso;

import java.util.List;

/**
 * Define las operaciones para asignar cursos a carreras.
 *
 * @author Edwin
 */
public interface CarreraCursoService {

    /**
     * Lista las asignaciones activas.
     *
     * @return asignaciones activas entre carreras y cursos
     */
    List<CarreraCurso> listarAsignaciones();

    /**
     * Obtiene una asignacion por identificador.
     *
     * @param id identificador de la asignacion
     * @return asignacion encontrada
     */
    CarreraCurso obtenerPorId(Long id);

    /**
     * Crea o actualiza una asignacion carrera-curso.
     *
     * @param carreraCurso asignacion a guardar
     * @return asignacion persistida
     */
    CarreraCurso guardarAsignacion(CarreraCurso carreraCurso);

    /**
     * Desactiva logicamente una asignacion.
     *
     * @param id identificador de la asignacion
     */
    void desactivarAsignacion(Long id);
}

