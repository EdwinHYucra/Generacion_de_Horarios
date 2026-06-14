package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.Docente;

import java.util.List;

/**
 * Define las operaciones disponibles para administrar docentes.
 *
 * @author Edwin
 */
public interface DocenteService {

    /**
     * Lista los docentes registrados.
     *
     * @return docentes registrados
     */
    List<Docente> listarDocentes();

    /**
     * Obtiene un docente por identificador.
     *
     * @param id identificador del docente
     * @return docente encontrado
     */
    Docente obtenerPorId(Long id);

    /**
     * Crea o actualiza un docente.
     *
     * @param docente docente a guardar
     * @return docente persistido
     */
    Docente guardarDocente(Docente docente);

    /**
     * Desactiva logicamente un docente.
     *
     * @param id identificador del docente
     */
    void desactivarDocente(Long id);
}

