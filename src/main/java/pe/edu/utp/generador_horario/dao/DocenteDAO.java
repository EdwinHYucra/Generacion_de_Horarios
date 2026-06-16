package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Docente;

import java.util.List;
import java.util.Optional;

public interface DocenteDAO {
    List<Docente> findAll();

    Optional<Docente> findById(Long id);

    Optional<Docente> findByUsuarioId(Long usuarioId);

    Docente save(Docente docente);
}
