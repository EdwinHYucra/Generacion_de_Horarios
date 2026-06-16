package pe.edu.utp.generador_horario.dao;

import java.util.List;

public interface DocenteCursoDAO {
    List<Long> findCursoIdsByDocenteId(Long idDocente);

    void deleteByDocenteId(Long idDocente);

    void save(Long idDocente, Long idCurso);
}