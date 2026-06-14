package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.CarreraDAO;
import pe.edu.utp.generador_horario.entidad.Carrera;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CarreraDAOImpl implements CarreraDAO {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Carrera> mapper = (rs, rowNum) -> {
        Carrera carrera = new Carrera();
        carrera.setIdCarrera(rs.getLong("id_carrera"));
        carrera.setCodigo(rs.getString("codigo"));
        carrera.setNombre(rs.getString("nombre"));
        carrera.setEstado(rs.getBoolean("estado"));
        return carrera;
    };

    public CarreraDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Carrera> findByEstadoTrue() {
        return jdbcTemplate.query("SELECT * FROM carreras WHERE estado = TRUE ORDER BY id_carrera DESC", mapper);
    }

    public Optional<Carrera> findById(Long id) {
        List<Carrera> carreras = jdbcTemplate.query("SELECT * FROM carreras WHERE id_carrera = ?", mapper, id);
        return carreras.isEmpty() ? Optional.empty() : Optional.of(carreras.get(0));
    }

    public Carrera save(Carrera carrera) {
        if (carrera.getIdCarrera() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO carreras (codigo, nombre, estado) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, carrera.getCodigo());
                ps.setString(2, carrera.getNombre());
                ps.setBoolean(3, Boolean.TRUE.equals(carrera.getEstado()));
                return ps;
            }, keyHolder);
            carrera.setIdCarrera(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update("UPDATE carreras SET codigo = ?, nombre = ?, estado = ? WHERE id_carrera = ?",
                    carrera.getCodigo(), carrera.getNombre(), Boolean.TRUE.equals(carrera.getEstado()), carrera.getIdCarrera());
        }
        return carrera;
    }

    public boolean existsByCodigo(String codigo) {
        return exists("SELECT COUNT(*) FROM carreras WHERE codigo = ?", codigo);
    }

    public boolean existsByNombre(String nombre) {
        return exists("SELECT COUNT(*) FROM carreras WHERE nombre = ?", nombre);
    }

    public boolean existsByCodigoAndIdCarreraNot(String codigo, Long idCarrera) {
        return exists("SELECT COUNT(*) FROM carreras WHERE codigo = ? AND id_carrera <> ?", codigo, idCarrera);
    }

    public boolean existsByNombreAndIdCarreraNot(String nombre, Long idCarrera) {
        return exists("SELECT COUNT(*) FROM carreras WHERE nombre = ? AND id_carrera <> ?", nombre, idCarrera);
    }

    private boolean exists(String sql, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }
}
