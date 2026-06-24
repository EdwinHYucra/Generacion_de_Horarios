package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.RestriccionSede;

import java.util.List;

/**
 * Define la administracion de reglas de traslado entre sedes.
 */
public interface RestriccionSedeService {

    /**
     * Lista todas las reglas de traslado configuradas.
     *
     * @return reglas ordenadas por sede de origen y destino
     */
    List<RestriccionSede> listarRestricciones();

    /**
     * Obtiene una regla por identificador.
     *
     * @param id identificador de la regla
     * @return regla encontrada
     */
    RestriccionSede obtenerPorId(Long id);

    /**
     * Crea o actualiza una regla de traslado.
     *
     * @param restriccionSede regla a persistir
     * @return regla persistida
     */
    RestriccionSede guardar(RestriccionSede restriccionSede);

    /**
     * Elimina una regla de traslado.
     *
     * @param id identificador de la regla
     */
    void eliminar(Long id);
}
