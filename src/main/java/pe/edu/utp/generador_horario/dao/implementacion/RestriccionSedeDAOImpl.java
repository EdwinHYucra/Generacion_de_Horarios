package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.RestriccionSedeDAO;
import pe.edu.utp.generador_horario.entidad.RestriccionSede;
import pe.edu.utp.generador_horario.entidad.Sede;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC para leer tiempos minimos de traslado entre sedes.
 */
@Repository
public class RestriccionSedeDAOImpl implements RestriccionSedeDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<RestriccionSede> mapper = (rs, rowNum) -> {
        Sede origen = new Sede();
        origen.setIdSede(rs.getLong("origen_id"));
        origen.setCodigo(rs.getString("origen_codigo"));
        origen.setNombre(rs.getString("origen_nombre"));

        Sede destino = new Sede();
        destino.setIdSede(rs.getLong("destino_id"));
        destino.setCodigo(rs.getString("destino_codigo"));
        destino.setNombre(rs.getString("destino_nombre"));

        RestriccionSede restriccion = new RestriccionSede();
        restriccion.setIdRestriccion(rs.getLong("id_restriccion"));
        restriccion.setSedeOrigen(origen);
        restriccion.setSedeDestino(destino);
        restriccion.setTiempoMinimoMinutos(rs.getInt("tiempo_minimo_minutos"));
        return restriccion;
    };

    public RestriccionSedeDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RestriccionSede> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT r.id_restriccion,
                               r.tiempo_minimo_minutos,
                               so.id_sede AS origen_id,
                               so.codigo AS origen_codigo,
                               so.nombre AS origen_nombre,
                               sd.id_sede AS destino_id,
                               sd.codigo AS destino_codigo,
                               sd.nombre AS destino_nombre
                        FROM restriccion_sede r
                        INNER JOIN sedes so ON so.id_sede = r.sede_origen
                        INNER JOIN sedes sd ON sd.id_sede = r.sede_destino
                        ORDER BY so.nombre, sd.nombre
                        """,
                mapper);
    }

    @Override
    public Optional<RestriccionSede> findById(Long id) {
        List<RestriccionSede> reglas = jdbcTemplate.query(
                """
                        SELECT r.id_restriccion,
                               r.tiempo_minimo_minutos,
                               so.id_sede AS origen_id,
                               so.codigo AS origen_codigo,
                               so.nombre AS origen_nombre,
                               sd.id_sede AS destino_id,
                               sd.codigo AS destino_codigo,
                               sd.nombre AS destino_nombre
                        FROM restriccion_sede r
                        INNER JOIN sedes so ON so.id_sede = r.sede_origen
                        INNER JOIN sedes sd ON sd.id_sede = r.sede_destino
                        WHERE r.id_restriccion = ?
                        """,
                mapper,
                id);

        return reglas.isEmpty() ? Optional.empty() : Optional.of(reglas.get(0));
    }

    @Override
    public RestriccionSede save(RestriccionSede restriccionSede) {
        Long sedeOrigen = restriccionSede.getSedeOrigen().getIdSede();
        Long sedeDestino = restriccionSede.getSedeDestino().getIdSede();

        if (restriccionSede.getIdRestriccion() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        """
                                INSERT INTO restriccion_sede
                                (sede_origen, sede_destino, tiempo_minimo_minutos)
                                VALUES (?, ?, ?)
                                """,
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, sedeOrigen);
                ps.setLong(2, sedeDestino);
                ps.setInt(3, restriccionSede.getTiempoMinimoMinutos());
                return ps;
            }, keyHolder);
            restriccionSede.setIdRestriccion(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                    """
                            UPDATE restriccion_sede
                            SET sede_origen = ?,
                                sede_destino = ?,
                                tiempo_minimo_minutos = ?
                            WHERE id_restriccion = ?
                            """,
                    sedeOrigen,
                    sedeDestino,
                    restriccionSede.getTiempoMinimoMinutos(),
                    restriccionSede.getIdRestriccion());
        }

        return restriccionSede;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM restriccion_sede WHERE id_restriccion = ?", id);
    }

    @Override
    public boolean existsBySedes(Long sedeOrigen, Long sedeDestino) {
        return exists(
                "SELECT COUNT(*) FROM restriccion_sede WHERE sede_origen = ? AND sede_destino = ?",
                sedeOrigen,
                sedeDestino);
    }

    @Override
    public boolean existsBySedesAndIdNot(Long sedeOrigen, Long sedeDestino, Long idRestriccion) {
        return exists(
                """
                        SELECT COUNT(*)
                        FROM restriccion_sede
                        WHERE sede_origen = ?
                          AND sede_destino = ?
                          AND id_restriccion <> ?
                        """,
                sedeOrigen,
                sedeDestino,
                idRestriccion);
    }

    @Override
    public Optional<Integer> obtenerTiempoMinimo(Long sedeOrigen, Long sedeDestino) {
        // La regla se consulta en el sentido exacto origen -> destino.
        // Si ambos sentidos son validos, deben registrarse dos filas.
        List<Integer> tiempos = jdbcTemplate.queryForList(
                """
                        SELECT tiempo_minimo_minutos
                        FROM restriccion_sede
                        WHERE sede_origen = ?
                          AND sede_destino = ?
                        LIMIT 1
                        """,
                Integer.class,
                sedeOrigen,
                sedeDestino);

        return tiempos.isEmpty() ? Optional.empty() : Optional.of(tiempos.get(0));
    }

    private boolean exists(String sql, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return count != null && count > 0;
    }
}
