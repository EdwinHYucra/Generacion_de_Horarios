package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;

import java.time.LocalTime;
import java.util.List;

@Repository
public class DisponibilidadDocenteDAOImpl implements DisponibilidadDocenteDAO {

    private final JdbcTemplate jdbcTemplate;

    public DisponibilidadDocenteDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DisponibilidadDocente> mapper = (rs, rowNum) -> {
        DisponibilidadDocente d = new DisponibilidadDocente();
        d.setIdDisponibilidad(rs.getLong("id_disponibilidad"));
        d.setIdCicloAcademico(rs.getLong("id_ciclo_academico"));
        d.setIdDocente(rs.getLong("id_docente"));
        d.setDiaSemana(rs.getString("dia_semana"));
        d.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
        d.setHoraFin(rs.getTime("hora_fin").toLocalTime());
        d.setEstado(rs.getBoolean("estado"));
        return d;
    };

    @Override
    public List<DisponibilidadDocente> findByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico) {
        return jdbcTemplate.query(
                """
                        SELECT *
                        FROM disponibilidad_docente
                        WHERE id_docente = ?
                          AND id_ciclo_academico = ?
                          AND estado = TRUE
                        ORDER BY dia_semana, hora_inicio
                        """,
                mapper,
                idDocente,
                idCicloAcademico);
    }

    @Override
    public long countBloquesDisponiblesEnRango(
            Long idDocente,
            Long idCicloAcademico,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin) {
        Long count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM disponibilidad_docente
                        WHERE id_docente = ?
                          AND id_ciclo_academico = ?
                          AND dia_semana = ?
                          AND hora_inicio <= ?
                          AND hora_fin >= ?
                          AND estado = TRUE
                        """,
                Long.class,
                idDocente,
                idCicloAcademico,
                diaSemana,
                horaInicio,
                horaFin);

        return count == null ? 0 : count;
    }

    @Override
    public void deleteByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico) {
        jdbcTemplate.update(
                "DELETE FROM disponibilidad_docente WHERE id_docente = ? AND id_ciclo_academico = ?",
                idDocente,
                idCicloAcademico);
    }

    @Override
    public void save(DisponibilidadDocente disponibilidad) {
        jdbcTemplate.update("""
                INSERT INTO disponibilidad_docente
                (id_ciclo_academico, id_docente, dia_semana, hora_inicio, hora_fin, estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                disponibilidad.getIdCicloAcademico(),
                disponibilidad.getIdDocente(),
                disponibilidad.getDiaSemana(),
                disponibilidad.getHoraInicio(),
                disponibilidad.getHoraFin(),
                Boolean.TRUE.equals(disponibilidad.getEstado()));
    }
}
