package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.SolicitudCambioHorarioDAO;
import pe.edu.utp.generador_horario.entidad.SolicitudCambioHorario;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SolicitudCambioHorarioDAOImpl implements SolicitudCambioHorarioDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SolicitudCambioHorario> mapper = (rs, rowNum) -> {
        SolicitudCambioHorario solicitud = new SolicitudCambioHorario();
        solicitud.setIdSolicitud(rs.getLong("id_comentario"));
        solicitud.setCodigoSolicitud(rs.getString("codigo_solicitud"));
        solicitud.setIdHorario(rs.getLong("id_horario"));
        solicitud.setIdDocente(rs.getLong("id_docente"));
        solicitud.setDocente(rs.getString("docente"));
        solicitud.setComentarioDocente(rs.getString("comentario"));
        solicitud.setTipoSolicitud(rs.getString("tipo_solicitud"));
        solicitud.setEstadoSolicitud(rs.getString("estado_solicitud"));
        solicitud.setIdAdministrador(rs.getObject("id_administrador", Long.class));
        solicitud.setComentarioAdministrador(rs.getString("comentario_administrador"));
        solicitud.setFechaRegistro(rs.getString("fecha_registro"));
        solicitud.setFechaResolucion(rs.getString("fecha_resolucion"));
        solicitud.setOpcionHorario(rs.getInt("opcion"));
        return solicitud;
    };

    public SolicitudCambioHorarioDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long registrar(SolicitudCambioHorario solicitud) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO comentario_horario
                            (codigo_solicitud, id_horario, id_docente, comentario, tipo_solicitud, estado_solicitud)
                            VALUES (?, ?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, solicitud.getCodigoSolicitud());
            ps.setLong(2, solicitud.getIdHorario());
            ps.setLong(3, solicitud.getIdDocente());
            ps.setString(4, solicitud.getComentarioDocente());
            ps.setString(5, solicitud.getTipoSolicitud());
            ps.setString(6, solicitud.getEstadoSolicitud());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<SolicitudCambioHorario> findById(Long idSolicitud) {
        List<SolicitudCambioHorario> solicitudes = jdbcTemplate.query(
                baseSelect() + " WHERE ch.id_comentario = ?",
                mapper,
                idSolicitud);
        return solicitudes.isEmpty() ? Optional.empty() : Optional.of(solicitudes.get(0));
    }

    @Override
    public List<SolicitudCambioHorario> findByDocente(Long idDocente) {
        return jdbcTemplate.query(
                baseSelect() + " WHERE ch.id_docente = ? ORDER BY ch.fecha_registro DESC, ch.id_comentario DESC",
                mapper,
                idDocente);
    }

    @Override
    public List<SolicitudCambioHorario> findAll() {
        return jdbcTemplate.query(
                baseSelect() + " ORDER BY ch.fecha_registro DESC, ch.id_comentario DESC",
                mapper);
    }

    @Override
    public void tomarEnRevision(Long idSolicitud, Long idAdministrador) {
        jdbcTemplate.update(
                """
                        UPDATE comentario_horario
                        SET estado_solicitud = 'EN_REVISION',
                            id_administrador = ?,
                            comentario_administrador = NULL,
                            fecha_resolucion = NULL
                        WHERE id_comentario = ?
                        """,
                idAdministrador,
                idSolicitud);
    }

    @Override
    public void responder(Long idSolicitud, Long idAdministrador, String estado, String comentarioAdministrador) {
        jdbcTemplate.update(
                """
                        UPDATE comentario_horario
                        SET estado_solicitud = ?,
                            id_administrador = ?,
                            comentario_administrador = ?,
                            fecha_resolucion = CASE
                                WHEN ? IN ('APROBADA', 'RECHAZADA') THEN CURRENT_TIMESTAMP
                                ELSE fecha_resolucion
                            END
                        WHERE id_comentario = ?
                        """,
                estado,
                idAdministrador,
                comentarioAdministrador,
                estado,
                idSolicitud);
    }

    @Override
    public void registrarHistorial(
            Long idSolicitud,
            Long idAdministrador,
            String estadoAnterior,
            String estadoNuevo,
            String accion,
            String comentario) {
        jdbcTemplate.update(
                """
                        INSERT INTO historial_solicitud_horario
                        (id_solicitud, id_administrador, estado_anterior, estado_nuevo, accion, comentario)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                idSolicitud,
                idAdministrador,
                estadoAnterior,
                estadoNuevo,
                accion,
                comentario);
    }

    private String baseSelect() {
        return """
                SELECT ch.id_comentario,
                       ch.codigo_solicitud,
                       ch.id_horario,
                       ch.id_docente,
                       CONCAT(d.nombres, ' ', d.apellidos) AS docente,
                       ch.comentario,
                       ch.tipo_solicitud,
                       ch.estado_solicitud,
                       ch.id_administrador,
                       ch.comentario_administrador,
                       DATE_FORMAT(ch.fecha_registro, '%Y-%m-%d %H:%i') AS fecha_registro,
                       DATE_FORMAT(ch.fecha_resolucion, '%Y-%m-%d %H:%i') AS fecha_resolucion,
                       h.opcion
                FROM comentario_horario ch
                INNER JOIN docentes d ON d.id_docente = ch.id_docente
                INNER JOIN horario_generado h ON h.id_horario = ch.id_horario
                """;
    }
}
