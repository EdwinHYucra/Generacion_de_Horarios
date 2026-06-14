package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.AulaDAO;
import pe.edu.utp.generador_horario.entidad.Aula;
import pe.edu.utp.generador_horario.entidad.Sede;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AulaDAOImpl implements AulaDAO {
    private static final String SELECT_BASE = """
            SELECT a.*, s.codigo AS sede_codigo, s.nombre AS sede_nombre, s.direccion AS sede_direccion, s.estado AS sede_estado
            FROM aulas a
            INNER JOIN sedes s ON s.id_sede = a.id_sede
            """;

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Aula> mapper = (rs, rowNum) -> {
        Sede sede = new Sede();
        sede.setIdSede(rs.getLong("id_sede"));
        sede.setCodigo(rs.getString("sede_codigo"));
        sede.setNombre(rs.getString("sede_nombre"));
        sede.setDireccion(rs.getString("sede_direccion"));
        sede.setEstado(rs.getBoolean("sede_estado"));

        Aula aula = new Aula();
        aula.setIdAula(rs.getLong("id_aula"));
        aula.setCodigo(rs.getString("codigo"));
        aula.setNombre(rs.getString("nombre"));
        aula.setTipo(rs.getString("tipo"));
        aula.setCapacidad(rs.getInt("capacidad"));
        aula.setUbicacion(rs.getString("ubicacion"));
        aula.setSede(sede);
        aula.setEstado(rs.getBoolean("estado"));
        return aula;
    };

    public AulaDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Aula> findByEstadoTrue() {
        return jdbcTemplate.query(SELECT_BASE + " WHERE a.estado = TRUE ORDER BY a.id_aula DESC", mapper);
    }

    public Optional<Aula> findById(Long id) {
        List<Aula> aulas = jdbcTemplate.query(SELECT_BASE + " WHERE a.id_aula = ?", mapper, id);
        return aulas.isEmpty() ? Optional.empty() : Optional.of(aulas.get(0));
    }

    public Aula save(Aula aula) {
        Long idSede = aula.getSede().getIdSede();
        if (aula.getIdAula() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO aulas (codigo, nombre, tipo, capacidad, ubicacion, id_sede, estado) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, aula.getCodigo());
                ps.setString(2, aula.getNombre());
                ps.setString(3, aula.getTipo());
                ps.setInt(4, aula.getCapacidad());
                ps.setString(5, aula.getUbicacion());
                ps.setLong(6, idSede);
                ps.setBoolean(7, Boolean.TRUE.equals(aula.getEstado()));
                return ps;
            }, keyHolder);
            aula.setIdAula(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update("""
                            UPDATE aulas
                            SET codigo = ?, nombre = ?, tipo = ?, capacidad = ?, ubicacion = ?, id_sede = ?, estado = ?
                            WHERE id_aula = ?
                            """,
                    aula.getCodigo(), aula.getNombre(), aula.getTipo(), aula.getCapacidad(), aula.getUbicacion(),
                    idSede, Boolean.TRUE.equals(aula.getEstado()), aula.getIdAula());
        }
        return aula;
    }

    public boolean existsByCodigo(String codigo) {
        return exists("SELECT COUNT(*) FROM aulas WHERE codigo = ?", codigo);
    }

    public boolean existsByCodigoAndIdAulaNot(String codigo, Long idAula) {
        return exists("SELECT COUNT(*) FROM aulas WHERE codigo = ? AND id_aula <> ?", codigo, idAula);
    }

    private boolean exists(String sql, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }
}
