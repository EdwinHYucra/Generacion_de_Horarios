package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.Aula;

import java.util.List;

/**
 * Define las operaciones disponibles para administrar aulas.
 *
 * @author Edwin
 */
public interface AulaService {

    /**
     * Lista las aulas activas del sistema.
     *
     * @return aulas activas
     */
    List<Aula> listarAulas();

    /**
     * Obtiene un aula por identificador.
     *
     * @param id identificador del aula
     * @return aula encontrada
     */
    Aula obtenerPorId(Long id);

    /**
     * Crea o actualiza un aula.
     *
     * @param aula aula a guardar
     * @return aula persistida
     */
    Aula guardarAula(Aula aula);

    /**
     * Desactiva logicamente un aula.
     *
     * @param id identificador del aula
     */
    void desactivarAula(Long id);
}

