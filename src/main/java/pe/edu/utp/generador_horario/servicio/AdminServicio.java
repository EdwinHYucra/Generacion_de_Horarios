package pe.edu.utp.generador_horario.servicio;

import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Casos de uso disponibles para la gestion de administradores.
 */
public interface AdminServicio {

    /**
     * Registra un nuevo administrador creado por un SuperAdmin.
     */
    void registrarAdmin(AdminRegistroDTO dto, Long superAdminId);

    /**
     * Lista administradores registrados.
     */
    List<Usuario> listarAdmins();

    /**
     * Obtiene un administrador por id de usuario.
     */
    Optional<Usuario> buscarPorId(Long id);

    /**
     * Actualiza datos editables de un administrador.
     */
    void actualizarAdmin(Usuario usuario);

    /**
     * Desactiva logicamente un administrador.
     */
    void desactivarAdmin(Long id);

    /**
     * Activa logicamente un administrador.
     */
    void activarAdmin(Long id);
}
