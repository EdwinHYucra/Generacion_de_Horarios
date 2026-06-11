package pe.edu.utp.generador_horario.servicio.implementacion;

import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dao.AdminDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.servicio.AdminServicio;
import pe.edu.utp.generador_horario.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServicioImpl implements AdminServicio {

    private final AdminDAO adminDAO;
    private final UsuarioDAO usuarioDAO;
    private final PasswordUtil passwordUtil;

    public AdminServicioImpl(AdminDAO adminDAO, UsuarioDAO usuarioDAO, PasswordUtil passwordUtil) {
        this.adminDAO = adminDAO;
        this.usuarioDAO = usuarioDAO;
        this.passwordUtil = passwordUtil;
    }

    @Override
    public void registrarAdmin(AdminRegistroDTO dto, Long superAdminId) {
        if (usuarioDAO.existeEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordUtil.encriptar(dto.getPassword()));
        usuario.setRol("ADMIN");
        usuario.setEstado("ACTIVO");

        adminDAO.guardar(usuario, superAdminId);
    }

    @Override
    public List<Usuario> listarAdmins() {
        return adminDAO.listarTodos();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return adminDAO.buscarPorId(id);
    }

    @Override
    public void actualizarAdmin(Usuario usuario) {
        adminDAO.actualizar(usuario);
    }

    @Override
    public void desactivarAdmin(Long id) {
        adminDAO.cambiarEstado(id, "INACTIVO");
    }

    @Override
    public void activarAdmin(Long id) {
        adminDAO.cambiarEstado(id, "ACTIVO");
    }
}