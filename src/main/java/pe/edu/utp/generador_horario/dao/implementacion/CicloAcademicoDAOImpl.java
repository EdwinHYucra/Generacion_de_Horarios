package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;

import java.util.List;
import java.util.Optional;

@Repository
public class CicloAcademicoDAOImpl implements CicloAcademicoDAO {

    private final JdbcTemplate jdbcTemplate;

    public CicloAcademicoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Long> findIdActivo() {
        List<Long> ids = jdbcTemplate.queryForList(
                """
                        SELECT id_ciclo_academico
                        FROM ciclos_academicos
                        WHERE activo = TRUE
                        ORDER BY fecha_inicio DESC, id_ciclo_academico DESC
                        LIMIT 1
                        """,
                Long.class);

        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }

    @Override
    public Optional<Long> findIdAnteriorAlActivo() {
        List<Long> ids = jdbcTemplate.queryForList(
                """
                        SELECT anterior.id_ciclo_academico
                        FROM ciclos_academicos anterior
                        JOIN ciclos_academicos activo ON activo.activo = TRUE
                        WHERE anterior.fecha_fin < activo.fecha_inicio
                        ORDER BY anterior.fecha_fin DESC, anterior.id_ciclo_academico DESC
                        LIMIT 1
                        """,
                Long.class);

        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }
}
