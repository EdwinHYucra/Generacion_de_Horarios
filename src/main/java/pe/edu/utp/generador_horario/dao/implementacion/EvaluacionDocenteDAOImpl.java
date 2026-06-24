package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.EvaluacionDocenteDAO;
import pe.edu.utp.generador_horario.dto.DocenteCursoEvaluacionDTO;

import java.util.List;
import java.util.Optional;

@Repository
public class EvaluacionDocenteDAOImpl implements EvaluacionDocenteDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<DocenteCursoEvaluacionDTO> mapper = (rs, rowNum) -> {
        DocenteCursoEvaluacionDTO dto = new DocenteCursoEvaluacionDTO();
        dto.setIdDocente(rs.getLong("id_docente"));
        dto.setNombreDocente(rs.getString("nombre_docente"));
        dto.setIdCurso(rs.getLong("id_curso"));
        dto.setNombreCurso(rs.getString("nombre_curso"));
        dto.setCicloAcademico(rs.getString("ciclo_academico"));
        return dto;
    };

    public EvaluacionDocenteDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<DocenteCursoEvaluacionDTO> listarDocentesCursosEvaluables(Long idCicloAcademico) {
        return jdbcTemplate.query(
                """
                        SELECT d.id_docente,
                               CONCAT(d.nombres, ' ', d.apellidos) AS nombre_docente,
                               c.id_curso,
                               c.nombre AS nombre_curso,
                               ca.nombre AS ciclo_academico
                        FROM docente_curso dc
                        INNER JOIN docentes d ON d.id_docente = dc.id_docente
                        INNER JOIN cursos c ON c.id_curso = dc.id_curso
                        INNER JOIN ciclos_academicos ca ON ca.id_ciclo_academico = dc.id_ciclo_academico
                        WHERE dc.id_ciclo_academico = ?
                          AND d.estado = TRUE
                          AND c.estado = TRUE
                        ORDER BY d.apellidos, d.nombres, c.nombre
                        """,
                mapper,
                idCicloAcademico);
    }

    @Override
    public boolean existeDocenteCursoEnCiclo(Long idCicloAcademico, Long idDocente, Long idCurso) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM docente_curso
                        WHERE id_ciclo_academico = ?
                          AND id_docente = ?
                          AND id_curso = ?
                        """,
                Integer.class,
                idCicloAcademico,
                idDocente,
                idCurso);

        return count != null && count > 0;
    }

    @Override
    public Optional<Double> obtenerPromedioPuntaje(Long idCicloAcademico, Long idDocente, Long idCurso) {
        List<Double> promedios = jdbcTemplate.queryForList(
                """
                        SELECT AVG(puntaje)
                        FROM evaluacion_docente
                        WHERE id_ciclo_academico = ?
                          AND id_docente = ?
                          AND id_curso = ?
                        """,
                Double.class,
                idCicloAcademico,
                idDocente,
                idCurso);

        return promedios.isEmpty() || promedios.get(0) == null
                ? Optional.empty()
                : Optional.of(promedios.get(0));
    }

    @Override
    public void guardar(Long idCicloAcademico, Long idDocente, Long idCurso, Integer puntaje, String categoria,
            String comentario) {
        jdbcTemplate.update(
                """
                        INSERT INTO evaluacion_docente
                        (id_ciclo_academico, id_docente, id_curso, puntaje, categoria, comentario)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                idCicloAcademico,
                idDocente,
                idCurso,
                puntaje,
                categoria,
                comentario);
    }
}
