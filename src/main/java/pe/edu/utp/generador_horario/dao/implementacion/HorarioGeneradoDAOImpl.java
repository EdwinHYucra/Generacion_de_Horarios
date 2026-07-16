package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.HorarioGeneradoDAO;
import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class HorarioGeneradoDAOImpl implements HorarioGeneradoDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<HorarioGeneradoResumenDTO> resumenMapper = (rs, rowNum) -> {
        HorarioGeneradoResumenDTO resumen = new HorarioGeneradoResumenDTO();
        resumen.setIdHorario(rs.getLong("id_horario"));
        resumen.setIdDocente(rs.getLong("id_docente"));
        resumen.setDocente(rs.getString("docente"));
        resumen.setOpcion(rs.getInt("opcion"));
        resumen.setEstado(rs.getString("estado"));
        resumen.setFechaGeneracion(rs.getString("fecha_generacion"));
        resumen.setTotalBloques(rs.getInt("total_bloques"));
        return resumen;
    };

    private final RowMapper<HorarioDetalleDTO> detalleMapper = (rs, rowNum) -> {
        HorarioDetalleDTO detalle = new HorarioDetalleDTO();
        detalle.setIdCurso(rs.getLong("id_curso"));
        detalle.setIdAula(rs.getLong("id_aula"));
        detalle.setCurso(rs.getString("curso"));
        detalle.setAula(rs.getString("aula"));
        detalle.setSede(rs.getString("sede"));
        detalle.setDia(rs.getString("dia_semana"));
        detalle.setHoraInicio(rs.getString("hora_inicio").substring(0, 5));
        detalle.setHoraFin(rs.getString("hora_fin").substring(0, 5));
        return detalle;
    };

    public HorarioGeneradoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long saveHorario(Long idDocente, OpcionesHorarioDTO opcion, String estado) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO horario_generado (id_docente, opcion, estado)
                            VALUES (?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, idDocente);
            ps.setInt(2, opcion.getOpcion());
            ps.setString(3, estado);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void saveDetalle(Long idHorario, HorarioDetalleDTO detalle) {
        jdbcTemplate.update(
                """
                        INSERT INTO horario_generado_detalle
                        (id_horario, id_curso, id_aula, dia_semana, hora_inicio, hora_fin)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                idHorario,
                detalle.getIdCurso(),
                detalle.getIdAula(),
                detalle.getDia(),
                Time.valueOf(LocalTime.parse(detalle.getHoraInicio())),
                Time.valueOf(LocalTime.parse(detalle.getHoraFin())));
    }

    @Override
    public void reemplazarDetalles(Long idHorario, List<HorarioDetalleDTO> detalles) {
        jdbcTemplate.update("DELETE FROM horario_generado_detalle WHERE id_horario = ?", idHorario);
        detalles.forEach(detalle -> saveDetalle(idHorario, detalle));
    }

    @Override
    public void eliminarPendientesPorDocente(Long idDocente) {
        List<Long> ids = jdbcTemplate.queryForList(
                """
                        SELECT id_horario
                        FROM horario_generado
                        WHERE id_docente = ?
                          AND estado = 'PENDIENTE'
                        """,
                Long.class,
                idDocente);

        for (Long id : ids) {
            jdbcTemplate.update("DELETE FROM horario_generado_detalle WHERE id_horario = ?", id);
            jdbcTemplate.update("DELETE FROM horario_generado WHERE id_horario = ?", id);
        }
    }

    @Override
    public void eliminarTodosPorDocente(Long idDocente) {
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id_horario FROM horario_generado WHERE id_docente = ?",
                Long.class,
                idDocente);
        for (Long id : ids) {
            jdbcTemplate.update("DELETE FROM comentario_horario WHERE id_horario = ?", id);
            jdbcTemplate.update("DELETE FROM horario_generado_detalle WHERE id_horario = ?", id);
            jdbcTemplate.update("DELETE FROM horario_generado WHERE id_horario = ?", id);
        }
    }

    @Override
    public List<HorarioGeneradoResumenDTO> listarResumenes() {
        return jdbcTemplate.query(
                """
                        SELECT h.id_horario,
                               h.id_docente,
                               CONCAT(d.nombres, ' ', d.apellidos) AS docente,
                               h.opcion,
                               h.estado,
                               DATE_FORMAT(h.fecha_generacion, '%Y-%m-%d %H:%i') AS fecha_generacion,
                               COUNT(det.id_detalle) AS total_bloques
                        FROM horario_generado h
                        INNER JOIN docentes d ON d.id_docente = h.id_docente
                        LEFT JOIN horario_generado_detalle det ON det.id_horario = h.id_horario
                        GROUP BY h.id_horario, h.id_docente, d.nombres, d.apellidos, h.opcion, h.estado, h.fecha_generacion
                        ORDER BY h.fecha_generacion DESC, h.id_horario DESC
                        """,
                resumenMapper);
    }

    @Override
    public List<HorarioGeneradoResumenDTO> listarPendientesPorDocente(Long idDocente) {
        return jdbcTemplate.query(
                """
                        SELECT h.id_horario,
                               h.id_docente,
                               CONCAT(d.nombres, ' ', d.apellidos) AS docente,
                               h.opcion,
                               h.estado,
                               DATE_FORMAT(h.fecha_generacion, '%Y-%m-%d %H:%i') AS fecha_generacion,
                               COUNT(det.id_detalle) AS total_bloques
                        FROM horario_generado h
                        INNER JOIN docentes d ON d.id_docente = h.id_docente
                        LEFT JOIN horario_generado_detalle det ON det.id_horario = h.id_horario
                        WHERE h.id_docente = ?
                          AND h.estado = 'PENDIENTE'
                        GROUP BY h.id_horario, h.id_docente, d.nombres, d.apellidos, h.opcion, h.estado, h.fecha_generacion
                        ORDER BY h.opcion ASC, h.id_horario ASC
                        """,
                resumenMapper,
                idDocente);
    }

    @Override
    public boolean existePorDocente(Long idHorario, Long idDocente) {
        Integer total = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(1)
                        FROM horario_generado
                        WHERE id_horario = ?
                          AND id_docente = ?
                        """,
                Integer.class,
                idHorario,
                idDocente);
        return total != null && total > 0;
    }

    @Override
    public Optional<String> findEstadoById(Long idHorario) {
        List<String> estados = jdbcTemplate.queryForList(
                "SELECT estado FROM horario_generado WHERE id_horario = ?",
                String.class,
                idHorario);
        return estados.isEmpty() ? Optional.empty() : Optional.ofNullable(estados.get(0));
    }

    @Override
    public Optional<HorarioGeneradoResumenDTO> buscarAprobadoPorDocente(Long idDocente) {
        List<HorarioGeneradoResumenDTO> horarios = jdbcTemplate.query(
                """
                        SELECT h.id_horario,
                               h.id_docente,
                               CONCAT(d.nombres, ' ', d.apellidos) AS docente,
                               h.opcion,
                               h.estado,
                               DATE_FORMAT(h.fecha_generacion, '%Y-%m-%d %H:%i') AS fecha_generacion,
                               COUNT(det.id_detalle) AS total_bloques
                        FROM horario_generado h
                        INNER JOIN docentes d ON d.id_docente = h.id_docente
                        LEFT JOIN horario_generado_detalle det ON det.id_horario = h.id_horario
                        WHERE h.id_docente = ?
                          AND h.estado IN ('APROBADO', 'APROBADA_DOCENTE')
                        GROUP BY h.id_horario, h.id_docente, d.nombres, d.apellidos, h.opcion, h.estado, h.fecha_generacion
                        ORDER BY CASE WHEN h.estado = 'APROBADO' THEN 0 ELSE 1 END,
                                 h.fecha_generacion DESC,
                                 h.id_horario DESC
                        LIMIT 1
                        """,
                resumenMapper,
                idDocente);

        return horarios.isEmpty() ? Optional.empty() : Optional.of(horarios.get(0));
    }

    @Override
    public List<HorarioDetalleDTO> listarDetalles(Long idHorario) {
        return jdbcTemplate.query(
                """
                        SELECT det.id_curso,
                               det.id_aula,
                               c.nombre AS curso,
                               a.nombre AS aula,
                               s.nombre AS sede,
                               det.dia_semana,
                               CAST(det.hora_inicio AS CHAR) AS hora_inicio,
                               CAST(det.hora_fin AS CHAR) AS hora_fin
                        FROM horario_generado_detalle det
                        INNER JOIN cursos c ON c.id_curso = det.id_curso
                        INNER JOIN aulas a ON a.id_aula = det.id_aula
                        INNER JOIN sedes s ON s.id_sede = a.id_sede
                        WHERE det.id_horario = ?
                        ORDER BY det.dia_semana, det.hora_inicio
                        """,
                detalleMapper,
                idHorario);
    }

    @Override
    public void actualizarEstado(Long idHorario, String estado) {
        jdbcTemplate.update(
                "UPDATE horario_generado SET estado = ? WHERE id_horario = ?",
                estado,
                idHorario);
    }

    @Override
    public void descartarPendientesDeDocenteExcepto(Long idHorario, Long idDocente) {
        jdbcTemplate.update(
                """
                        UPDATE horario_generado
                        SET estado = 'DESCARTADO'
                        WHERE id_docente = ?
                          AND id_horario <> ?
                          AND estado = 'PENDIENTE'
                        """,
                idDocente,
                idHorario);
    }

    @Override
    public void aprobar(Long idHorario) {
        Long docenteId = jdbcTemplate.queryForObject(
                "SELECT id_docente FROM horario_generado WHERE id_horario = ?",
                Long.class,
                idHorario);

        if (docenteId != null) {
            jdbcTemplate.update(
                    """
                            UPDATE horario_generado
                            SET estado = 'DESCARTADO'
                            WHERE id_docente = ?
                              AND id_horario <> ?
                              AND estado IN ('PENDIENTE', 'APROBADA_DOCENTE', 'EN_REVISION', 'APROBADO')
                            """,
                    docenteId,
                    idHorario);
        }

        jdbcTemplate.update("UPDATE horario_generado SET estado = 'APROBADO' WHERE id_horario = ?", idHorario);
    }
}
