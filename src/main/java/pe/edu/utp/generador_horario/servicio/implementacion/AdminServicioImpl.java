package pe.edu.utp.generador_horario.servicio.implementacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.generador_horario.config.EstadoUsuario;
import pe.edu.utp.generador_horario.dao.AdminDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.factory.UsuarioFactory;
import pe.edu.utp.generador_horario.servicio.AdminServicio;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicacion para la gestion de administradores.
 *
 * <p>Aplica SRP delegando la construccion de usuarios a {@link UsuarioFactory}
 * y dejando aqui la orquestacion del caso de uso.</p>
 */
@Service
public class AdminServicioImpl implements AdminServicio {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServicioImpl.class);

    private final AdminDAO adminDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioFactory usuarioFactory;

    public AdminServicioImpl(AdminDAO adminDAO, UsuarioDAO usuarioDAO, UsuarioFactory usuarioFactory) {
        this.adminDAO = adminDAO;
        this.usuarioDAO = usuarioDAO;
        this.usuarioFactory = usuarioFactory;
    }

    @Override
    @Transactional
    public void registrarAdmin(AdminRegistroDTO dto, Long superAdminId) {
        if (usuarioDAO.existeEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Usuario usuario = usuarioFactory.crearAdministrador(dto);
        adminDAO.guardar(usuario, superAdminId);
        LOGGER.info("Administrador registrado. email={}, creadoPorSuperAdmin={}", dto.getEmail(), superAdminId);
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
        adminDAO.cambiarEstado(id, EstadoUsuario.INACTIVO);
        LOGGER.info("Administrador desactivado. usuarioId={}", id);
    }

    @Override
    public void activarAdmin(Long id) {
        adminDAO.cambiarEstado(id, EstadoUsuario.ACTIVO);
        LOGGER.info("Administrador activado. usuarioId={}", id);
    }
}
