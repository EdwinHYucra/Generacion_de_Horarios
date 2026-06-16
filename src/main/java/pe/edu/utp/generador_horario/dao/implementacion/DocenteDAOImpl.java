package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.entidad.Docente;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DocenteDAOImpl implements DocenteDAO {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Docente> mapper = (rs, rowNum) -> {
        Docente docente = new Docente();
        docente.setIdDocente(rs.getLong("id_docente"));
        docente.setUsuarioId(rs.getLong("usuario_id"));
        docente.setCodigo(rs.getString("codigo"));
        docente.setNombres(rs.getString("nombres"));
        docente.setApellidos(rs.getString("apellidos"));
        docente.setDni(rs.getString("dni"));
        docente.setCorreo(rs.getString("correo"));
        docente.setCelular(rs.getString("celular"));
        docente.setEspecialidad(rs.getString("especialidad"));
        docente.setCarrera(rs.getString("carrera"));
        docente.setGradoAcademico(rs.getString("grado_academico"));
        docente.setTipoContrato(rs.getString("tipo_contrato"));
        docente.setObservaciones(rs.getString("observaciones"));
        docente.setEstado(rs.getBoolean("estado"));
        return docente;
    };

    public DocenteDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Docente> findAll() {
        return jdbcTemplate.query("SELECT * FROM docentes ORDER BY id_docente DESC", mapper);
    }

    @Override
    public Optional<Docente> findById(Long id) {
        List<Docente> docentes = jdbcTemplate.query(
                "SELECT * FROM docentes WHERE id_docente = ?",
                mapper,
                id);

        return docentes.isEmpty()
                ? Optional.empty()
                : Optional.of(docentes.get(0));
    }

    @Override
    public Optional<Docente> findByUsuarioId(Long usuarioId) {
        List<Docente> docentes = jdbcTemplate.query(
                "SELECT * FROM docentes WHERE usuario_id = ?",
                mapper,
                usuarioId);

        return docentes.isEmpty()
                ? Optional.empty()
                : Optional.of(docentes.get(0));
    }

    public Docente save(Docente docente) {
        if (docente.getIdDocente() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        """
                                INSERT INTO docentes
                                (usuario_id, codigo, nombres, apellidos, dni, correo, celular, especialidad, carrera, grado_academico, tipo_contrato, observaciones, estado)
                                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                """,
                        Statement.RETURN_GENERATED_KEYS);
                setValues(ps, docente);
                return ps;
            }, keyHolder);
            docente.setIdDocente(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(
                    """
                            UPDATE docentes
                            SET usuario_id = ?, codigo = ?, nombres = ?, apellidos = ?, dni = ?, correo = ?, celular = ?, especialidad = ?,
                                carrera = ?, grado_academico = ?, tipo_contrato = ?, observaciones = ?, estado = ?
                            WHERE id_docente = ?
                            """,
                    docente.getUsuarioId(), docente.getCodigo(), docente.getNombres(), docente.getApellidos(),
                    docente.getDni(),
                    docente.getCorreo(), docente.getCelular(), docente.getEspecialidad(), docente.getCarrera(),
                    docente.getGradoAcademico(),
                    docente.getTipoContrato(), docente.getObservaciones(), Boolean.TRUE.equals(docente.getEstado()),
                    docente.getIdDocente());
        }
        return docente;
    }

    private void setValues(PreparedStatement ps, Docente docente) throws java.sql.SQLException {
        ps.setLong(1, docente.getUsuarioId());
        ps.setString(2, docente.getCodigo());
        ps.setString(3, docente.getNombres());
        ps.setString(4, docente.getApellidos());
        ps.setString(5, docente.getDni());
        ps.setString(6, docente.getCorreo());
        ps.setString(7, docente.getCelular());
        ps.setString(8, docente.getEspecialidad());
        ps.setString(9, docente.getCarrera());
        ps.setString(10, docente.getGradoAcademico());
        ps.setString(11, docente.getTipoContrato());
        ps.setString(12, docente.getObservaciones());
        ps.setBoolean(13, Boolean.TRUE.equals(docente.getEstado()));
    }
}
