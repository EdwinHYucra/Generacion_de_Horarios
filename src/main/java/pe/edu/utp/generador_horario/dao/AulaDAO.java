package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Aula;

import java.util.List;
import java.util.Optional;

public interface AulaDAO {
    List<Aula> findByEstadoTrue();
    Optional<Aula> findById(Long id);
    Aula save(Aula aula);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdAulaNot(String codigo, Long idAula);
}
