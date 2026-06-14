package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Sede;

import java.util.List;
import java.util.Optional;

public interface SedeDAO {
    List<Sede> findByEstadoTrue();
    Optional<Sede> findById(Long id);
    Sede save(Sede sede);
    boolean existsByCodigo(String codigo);
    boolean existsByNombre(String nombre);
    boolean existsByCodigoAndIdSedeNot(String codigo, Long idSede);
    boolean existsByNombreAndIdSedeNot(String nombre, Long idSede);
}
