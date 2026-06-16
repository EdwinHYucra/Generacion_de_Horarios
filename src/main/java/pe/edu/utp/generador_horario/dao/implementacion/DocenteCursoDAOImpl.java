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
    public List<Long> findCursoIdsByDocenteId(Long idDocente) {
        return jdbcTemplate.queryForList(
                "SELECT id_curso FROM docente_curso WHERE id_docente = ?",
                Long.class,
                idDocente);
    }

    @Override
    public void deleteByDocenteId(Long idDocente) {
        jdbcTemplate.update(
                "DELETE FROM docente_curso WHERE id_docente = ?",
                idDocente);
    }

    @Override
    public void save(Long idDocente, Long idCurso) {
        jdbcTemplate.update("""
                INSERT INTO docente_curso (id_docente, id_curso)
                VALUES (?, ?)
                """,
                idDocente,
                idCurso);
    }
}