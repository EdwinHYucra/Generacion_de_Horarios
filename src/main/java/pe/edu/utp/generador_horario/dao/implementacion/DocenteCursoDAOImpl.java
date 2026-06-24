package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;

import java.util.List;

@Repository
public class DocenteCursoDAOImpl implements DocenteCursoDAO {

    private final JdbcTemplate jdbcTemplate;

    public DocenteCursoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> findCursoIdsByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico) {
        return jdbcTemplate.queryForList(
                "SELECT id_curso FROM docente_curso WHERE id_docente = ? AND id_ciclo_academico = ?",
                Long.class,
                idDocente,
                idCicloAcademico);
    }

    @Override
    public void deleteByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico) {
        jdbcTemplate.update(
                "DELETE FROM docente_curso WHERE id_docente = ? AND id_ciclo_academico = ?",
                idDocente,
                idCicloAcademico);
    }

    @Override
    public void save(Long idDocente, Long idCurso, Long idCicloAcademico) {
        jdbcTemplate.update("""
                INSERT INTO docente_curso (id_ciclo_academico, id_docente, id_curso)
                VALUES (?, ?, ?)
                """,
                idCicloAcademico,
                idDocente,
                idCurso);
    }
}
