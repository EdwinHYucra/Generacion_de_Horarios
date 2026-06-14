package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.Carrera;

import java.util.List;

/**
 * Define las operaciones disponibles para administrar carreras.
 *
 * @author Edwin
 */
public interface CarreraService {

    /**
     * Lista las carreras activas.
     *
     * @return carreras activas
     */
    List<Carrera> listarCarreras();

    /**
     * Obtiene una carrera por identificador.
     *
     * @param id identificador de la carrera
     * @return carrera encontrada
     */
    Carrera obtenerPorId(Long id);

    /**
     * Crea o actualiza una carrera.
     *
     * @param carrera carrera a guardar
     * @return carrera persistida
     */
    Carrera guardarCarrera(Carrera carrera);

    /**
     * Desactiva logicamente una carrera.
     *
     * @param id identificador de la carrera
     */
    void desactivarCarrera(Long id);
}

