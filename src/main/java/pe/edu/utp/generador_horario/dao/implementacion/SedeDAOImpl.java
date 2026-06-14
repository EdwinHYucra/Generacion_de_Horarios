package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.SedeDAO;
import pe.edu.utp.generador_horario.entidad.Sede;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SedeDAOImpl implements SedeDAO {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Sede> mapper = (rs, rowNum) -> {
        Sede sede = new Sede();
        sede.setIdSede(rs.getLong("id_sede"));
        sede.setCodigo(rs.getString("codigo"));
        sede.setNombre(rs.getString("nombre"));
        sede.setDireccion(rs.getString("direccion"));
        sede.setEstado(rs.getBoolean("estado"));
        return sede;
    };

    public SedeDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Sede> findByEstadoTrue() {
        return jdbcTemplate.query("SELECT * FROM sedes WHERE estado = TRUE ORDER BY id_sede DESC", mapper);
    }

    public Optional<Sede> findById(Long id) {
        List<Sede> sedes = jdbcTemplate.query("SELECT * FROM sedes WHERE id_sede = ?", mapper, id);
        return sedes.isEmpty() ? Optional.empty() : Optional.of(sedes.get(0));
    }

    public Sede save(Sede sede) {
        if (sede.getIdSede() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO sedes (codigo, nombre, direccion, estado) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, sede.getCodigo());
                ps.setString(2, sede.getNombre());
                ps.setString(3, sede.getDireccion());
                ps.setBoolean(4, Boolean.TRUE.equals(sede.getEstado()));
                return ps;
            }, keyHolder);
            sede.setIdSede(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update("UPDATE sedes SET codigo = ?, nombre = ?, direccion = ?, estado = ? WHERE id_sede = ?",
                    sede.getCodigo(), sede.getNombre(), sede.getDireccion(),
                    Boolean.TRUE.equals(sede.getEstado()), sede.getIdSede());
        }
        return sede;
    }

    public boolean existsByCodigo(String codigo) {
        return exists("SELECT COUNT(*) FROM sedes WHERE codigo = ?", codigo);
    }

    public boolean existsByNombre(String nombre) {
        return exists("SELECT COUNT(*) FROM sedes WHERE nombre = ?", nombre);
    }

    public boolean existsByCodigoAndIdSedeNot(String codigo, Long idSede) {
        return exists("SELECT COUNT(*) FROM sedes WHERE codigo = ? AND id_sede <> ?", codigo, idSede);
    }

    public boolean existsByNombreAndIdSedeNot(String nombre, Long idSede) {
        return exists("SELECT COUNT(*) FROM sedes WHERE nombre = ? AND id_sede <> ?", nombre, idSede);
    }

    private boolean exists(String sql, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }
}
