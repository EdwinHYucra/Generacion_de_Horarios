package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.CarreraCursoDAO;
import pe.edu.utp.generador_horario.entidad.Carrera;
import pe.edu.utp.generador_horario.entidad.CarreraCurso;
import pe.edu.utp.generador_horario.entidad.Curso;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CarreraCursoDAOImpl implements CarreraCursoDAO {
    private static final String SELECT_BASE = """
            SELECT cc.*,
                   c.codigo AS carrera_codigo, c.nombre AS carrera_nombre, c.estado AS carrera_estado,
                   cu.codigo AS curso_codigo, cu.nombre AS curso_nombre, cu.horas_semanales, cu.tipo AS curso_tipo, cu.estado AS curso_estado
            FROM carrera_curso cc
            INNER JOIN carreras c ON c.id_carrera = cc.id_carrera
            INNER JOIN cursos cu ON cu.id_curso = cc.id_curso
            """;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CarreraCurso> mapper = (rs, rowNum) -> {
        Carrera carrera = new Carrera();
        carrera.setIdCarrera(rs.getLong("id_carrera"));
        carrera.setCodigo(rs.getString("carrera_codigo"));
        carrera.setNombre(rs.getString("carrera_nombre"));
        carrera.setEstado(rs.getBoolean("carrera_estado"));

        Curso curso = new Curso();
        curso.setIdCurso(rs.getLong("id_curso"));
        curso.setCodigo(rs.getString("curso_codigo"));
        curso.setNombre(rs.getString("curso_nombre"));
        curso.setHorasSemanales(rs.getInt("horas_semanales"));
        curso.setTipo(rs.getString("curso_tipo"));
        curso.setEstado(rs.getBoolean("curso_estado"));

        CarreraCurso carreraCurso = new CarreraCurso();
        carreraCurso.setIdCarreraCurso(rs.getLong("id_carrera_curso"));
        carreraCurso.setCarrera(carrera);
        carreraCurso.setCurso(curso);
        carreraCurso.setCiclo(rs.getInt("ciclo"));
        carreraCurso.setEstado(rs.getBoolean("estado"));
        return carreraCurso;
    };

    public CarreraCursoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CarreraCurso> findByEstadoTrue() {
        return jdbcTemplate.query(SELECT_BASE + " WHERE cc.estado = TRUE ORDER BY cc.id_carrera_curso DESC", mapper);
    }

    public Optional<CarreraCurso> findById(Long id) {
        List<CarreraCurso> asignaciones = jdbcTemplate.query(SELECT_BASE + " WHERE cc.id_carrera_curso = ?", mapper, id);
        return asignaciones.isEmpty() ? Optional.empty() : Optional.of(asignaciones.get(0));
    }

    public CarreraCurso save(CarreraCurso carreraCurso) {
        Long idCarrera = carreraCurso.getCarrera().getIdCarrera();
        Long idCurso = carreraCurso.getCurso().getIdCurso();
        if (carreraCurso.getIdCarreraCurso() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO carrera_curso (id_carrera, id_curso, ciclo, estado) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, idCarrera);
                ps.setLong(2, idCurso);
                ps.setInt(3, carreraCurso.getCiclo());
                ps.setBoolean(4, Boolean.TRUE.equals(carreraCurso.getEstado()));
                return ps;
            }, keyHolder);
            carreraCurso.setIdCarreraCurso(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update("""
                            UPDATE carrera_curso
                            SET id_carrera = ?, id_curso = ?, ciclo = ?, estado = ?
                            WHERE id_carrera_curso = ?
                            """,
                    idCarrera, idCurso, carreraCurso.getCiclo(), Boolean.TRUE.equals(carreraCurso.getEstado()),
                    carreraCurso.getIdCarreraCurso());
        }
        return carreraCurso;
    }

    public boolean existsByCarrera_IdCarreraAndCurso_IdCurso(Long idCarrera, Long idCurso) {
        return exists("SELECT COUNT(*) FROM carrera_curso WHERE id_carrera = ? AND id_curso = ?", idCarrera, idCurso);
    }

    public boolean existsByCarrera_IdCarreraAndCurso_IdCursoAndIdCarreraCursoNot(Long idCarrera, Long idCurso, Long idCarreraCurso) {
        return exists("SELECT COUNT(*) FROM carrera_curso WHERE id_carrera = ? AND id_curso = ? AND id_carrera_curso <> ?",
                idCarrera, idCurso, idCarreraCurso);
    }

    private boolean exists(String sql, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }
}
