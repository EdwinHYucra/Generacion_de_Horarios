package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.RestriccionSede;

import java.util.List;
import java.util.Optional;

/**
 * DAO para consultar reglas de traslado entre sedes.
 */
public interface RestriccionSedeDAO {

    List<RestriccionSede> findAll();

    Optional<RestriccionSede> findById(Long id);

    RestriccionSede save(RestriccionSede restriccionSede);

    void deleteById(Long id);

    boolean existsBySedes(Long sedeOrigen, Long sedeDestino);

    boolean existsBySedesAndIdNot(Long sedeOrigen, Long sedeDestino, Long idRestriccion);

    /**
     * Obtiene el tiempo minimo de traslado entre dos sedes.
     *
     * @param sedeOrigen sede desde donde termina la clase previa
     * @param sedeDestino sede donde inicia la clase siguiente
     * @return minutos minimos requeridos, si existe regla configurada
     */
    Optional<Integer> obtenerTiempoMinimo(Long sedeOrigen, Long sedeDestino);
}
