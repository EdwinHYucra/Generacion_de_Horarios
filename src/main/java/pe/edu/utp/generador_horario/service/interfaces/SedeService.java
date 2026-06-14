package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.Sede;

import java.util.List;

/**
 * Define las operaciones disponibles para administrar sedes.
 *
 * @author Edwin
 */
public interface SedeService {

    /**
     * Lista las sedes activas.
     *
     * @return sedes activas
     */
    List<Sede> listarSedes();

    /**
     * Obtiene una sede por identificador.
     *
     * @param id identificador de la sede
     * @return sede encontrada
     */
    Sede obtenerPorId(Long id);

    /**
     * Crea o actualiza una sede.
     *
     * @param sede sede a guardar
     * @return sede persistida
     */
    Sede guardarSede(Sede sede);

    /**
     * Desactiva logicamente una sede.
     *
     * @param id identificador de la sede
     */
    void desactivarSede(Long id);
}

