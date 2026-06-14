package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Carrera;

import java.util.List;
import java.util.Optional;

public interface CarreraDAO {
    List<Carrera> findByEstadoTrue();
    Optional<Carrera> findById(Long id);
    Carrera save(Carrera carrera);
    boolean existsByCodigo(String codigo);
    boolean existsByNombre(String nombre);
    boolean existsByCodigoAndIdCarreraNot(String codigo, Long idCarrera);
    boolean existsByNombreAndIdCarreraNot(String nombre, Long idCarrera);
}
