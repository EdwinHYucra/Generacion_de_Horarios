package pe.edu.utp.generador_horario.dao.implementacion;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pe.edu.utp.generador_horario.config.EstadoUsuario;
import pe.edu.utp.generador_horario.config.RolSistema;
import pe.edu.utp.generador_horario.dao.AdminDAO;
import pe.edu.utp.generador_horario.entidad.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminDAOImpl implements AdminDAO {

    private final JdbcTemplate jdbcTemplate;

    public AdminDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Usuario> usuarioMapper = new RowMapper<Usuario>() {
        @Override
        public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Usuario u = new Usuario();
            u.setId(rs.getLong("id"));
            u.setNombre(rs.getString("nombre"));
            u.setApellido(rs.getString("apellido"));
            u.setEmail(rs.getString("email"));
            u.setRol(rs.getString("rol"));
            u.setEstado(rs.getString("estado"));
            return u;
        }
    };

    @Override
    public void guardar(Usuario usuario, Long superAdminId) {
        // DAO: solo persiste; la construccion del usuario queda en UsuarioFactory.
        String sqlUsuario = "INSERT INTO usuario (nombre, apellido, email, password, rol, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getPassword());
            ps.setString(5, usuario.getRol() == null ? RolSistema.ADMIN : usuario.getRol());
            ps.setString(6, usuario.getEstado() == null ? EstadoUsuario.ACTIVO : usuario.getEstado());
            return ps;
        }, keyHolder);

        Long usuarioId = keyHolder.getKey().longValue();
        String sqlAdmin = "INSERT INTO admin (usuario_id, creado_por) VALUES (?, ?)";
        jdbcTemplate.update(sqlAdmin, usuarioId, superAdminId);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT u.* FROM usuario u " +
                     "INNER JOIN admin a ON u.id = a.usuario_id " +
                     "WHERE u.id = ?";
        List<Usuario> lista = jdbcTemplate.query(sql, usuarioMapper, id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT u.* FROM usuario u " +
                     "INNER JOIN admin a ON u.id = a.usuario_id " +
                     "ORDER BY u.id DESC";
        return jdbcTemplate.query(sql, usuarioMapper);
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre = ?, apellido = ?, email = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail(),
            usuario.getId()
        );
    }

    @Override
    public void cambiarEstado(Long id, String estado) {
        String sql = "UPDATE usuario SET estado = ? WHERE id = ?";
        jdbcTemplate.update(sql, estado, id);
    }
}
