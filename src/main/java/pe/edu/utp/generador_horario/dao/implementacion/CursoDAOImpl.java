package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.CursoDAO;
import pe.edu.utp.generador_horario.entidad.Curso;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CursoDAOImpl implements CursoDAO {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Curso> mapper = (rs, rowNum) -> {
        Curso curso = new Curso();
        curso.setIdCurso(rs.getLong("id_curso"));
        curso.setCodigo(rs.getString("codigo"));
        curso.setNombre(rs.getString("nombre"));
        curso.setHorasSemanales(rs.getInt("horas_semanales"));
        curso.setTipo(rs.getString("tipo"));
        curso.setEstado(rs.getBoolean("estado"));
        return curso;
    };

    public CursoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Curso> findByEstadoTrue() {
        return jdbcTemplate.query("SELECT * FROM cursos WHERE estado = TRUE ORDER BY id_curso DESC", mapper);
    }

    public List<Curso> findByTipoAndEstadoTrue(String tipo) {
        return jdbcTemplate.query("SELECT * FROM cursos WHERE tipo = ? AND estado = TRUE ORDER BY nombre", mapper, tipo);
    }

    public Optional<Curso> findById(Long id) {
        List<Curso> cursos = jdbcTemplate.query("SELECT * FROM cursos WHERE id_curso = ?", mapper, id);
        return cursos.isEmpty() ? Optional.empty() : Optional.of(cursos.get(0));
    }

    public Curso save(Curso curso) {
        if (curso.getIdCurso() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO cursos (codigo, nombre, horas_semanales, tipo, estado) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, curso.getCodigo());
                ps.setString(2, curso.getNombre());
                ps.setInt(3, curso.getHorasSemanales());
                ps.setString(4, curso.getTipo());
                ps.setBoolean(5, Boolean.TRUE.equals(curso.getEstado()));
                return ps;
            }, keyHolder);
            curso.setIdCurso(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update("UPDATE cursos SET codigo = ?, nombre = ?, horas_semanales = ?, tipo = ?, estado = ? WHERE id_curso = ?",
                    curso.getCodigo(), curso.getNombre(), curso.getHorasSemanales(), curso.getTipo(),
                    Boolean.TRUE.equals(curso.getEstado()), curso.getIdCurso());
        }
        return curso;
    }

    public boolean existsByCodigo(String codigo) {
        return exists("SELECT COUNT(*) FROM cursos WHERE codigo = ?", codigo);
    }

    public boolean existsByNombre(String nombre) {
        return exists("SELECT COUNT(*) FROM cursos WHERE nombre = ?", nombre);
    }

    public boolean existsByCodigoAndIdCursoNot(String codigo, Long idCurso) {
        return exists("SELECT COUNT(*) FROM cursos WHERE codigo = ? AND id_curso <> ?", codigo, idCurso);
    }

    public boolean existsByNombreAndIdCursoNot(String nombre, Long idCurso) {
        return exists("SELECT COUNT(*) FROM cursos WHERE nombre = ? AND id_curso <> ?", nombre, idCurso);
    }

    private boolean exists(String sql, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }
}
