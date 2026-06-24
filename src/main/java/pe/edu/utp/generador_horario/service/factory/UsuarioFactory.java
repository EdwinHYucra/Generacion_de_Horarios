package pe.edu.utp.generador_horario.service.factory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.config.EstadoUsuario;
import pe.edu.utp.generador_horario.config.RolSistema;
import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;

/**
 * Factory simple para construir usuarios por rol.
 *
 * <p>Patron aplicado: Factory. Mantiene en un solo lugar la construccion de
 * usuarios con password cifrado, rol y estado inicial, dejando que los
 * servicios se concentren en orquestar la operacion de negocio.</p>
 */
@Component
public class UsuarioFactory {

    private final PasswordEncoder passwordEncoder;

    public UsuarioFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un usuario administrador activo desde el DTO de registro.
     *
     * @param dto datos capturados desde el formulario del SuperAdmin
     * @return usuario listo para persistir
     */
    public Usuario crearAdministrador(AdminRegistroDTO dto) {
        return crearUsuario(
                dto.getNombre(),
                dto.getApellido(),
                dto.getEmail(),
                dto.getPassword(),
                RolSistema.ADMIN,
                EstadoUsuario.ACTIVO);
    }

    /**
     * Crea el usuario de acceso asociado a un docente academico.
     *
     * @param docente datos del docente registrado por el administrador
     * @return usuario con rol docente listo para persistir
     */
    public Usuario crearDocente(Docente docente) {
        String estado = Boolean.TRUE.equals(docente.getEstado())
                ? EstadoUsuario.ACTIVO
                : EstadoUsuario.INACTIVO;

        return crearUsuario(
                docente.getNombres(),
                docente.getApellidos(),
                docente.getCorreo(),
                docente.getPassword(),
                RolSistema.DOCENTE,
                estado);
    }

    private Usuario crearUsuario(
            String nombre,
            String apellido,
            String email,
            String passwordPlano,
            String rol,
            String estado) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(passwordPlano));
        usuario.setRol(rol);
        usuario.setEstado(estado);
        return usuario;
    }
}
