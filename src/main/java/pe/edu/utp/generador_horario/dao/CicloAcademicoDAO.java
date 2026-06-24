package pe.edu.utp.generador_horario.dao;

import java.util.Optional;

/**
 * DAO para consultar el ciclo academico vigente.
 */
public interface CicloAcademicoDAO {

    /**
     * Obtiene el identificador del ciclo academico activo.
     *
     * @return id del ciclo activo, si existe
     */
    Optional<Long> findIdActivo();

    /**
     * Obtiene el ciclo academico inmediatamente anterior al ciclo activo.
     *
     * @return id del ciclo anterior, si existe
     */
    Optional<Long> findIdAnteriorAlActivo();
}
