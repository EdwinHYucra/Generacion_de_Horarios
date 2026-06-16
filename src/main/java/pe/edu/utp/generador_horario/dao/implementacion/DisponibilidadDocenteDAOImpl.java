package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;

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
        d.setIdDocente(rs.getLong("id_docente"));
        d.setDiaSemana(rs.getString("dia_semana"));
        d.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
        d.setHoraFin(rs.getTime("hora_fin").toLocalTime());
        d.setEstado(rs.getBoolean("estado"));
        return d;
    };

    @Override
    public List<DisponibilidadDocente> findByDocenteId(Long idDocente) {
        return jdbcTemplate.query(
                "SELECT * FROM disponibilidad_docente WHERE id_docente = ? AND estado = TRUE",
                mapper,
                idDocente);
    }

    @Override
    public void deleteByDocenteId(Long idDocente) {
        jdbcTemplate.update("DELETE FROM disponibilidad_docente WHERE id_docente = ?", idDocente);
    }

    @Override
    public void save(DisponibilidadDocente disponibilidad) {
        jdbcTemplate.update("""
                INSERT INTO disponibilidad_docente
                (id_docente, dia_semana, hora_inicio, hora_fin, estado)
                VALUES (?, ?, ?, ?, ?)
                """,
                disponibilidad.getIdDocente(),
                disponibilidad.getDiaSemana(),
                disponibilidad.getHoraInicio(),
                disponibilidad.getHoraFin(),
                Boolean.TRUE.equals(disponibilidad.getEstado()));
    }
}