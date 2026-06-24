package pe.edu.utp.generador_horario.dao;

import java.util.List;

public interface DocenteCursoDAO {
    List<Long> findCursoIdsByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico);

    void deleteByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico);

    void save(Long idDocente, Long idCurso, Long idCicloAcademico);
}
