package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Usuario;
import java.util.Optional;

/**
 * Contrato DAO para acceder a usuarios de autenticacion.
 */
public interface UsuarioDAO {

    /**
     * Busca un usuario por su email, usado como username de Spring Security.
     *
     * @param email correo institucional o de acceso
     * @return usuario encontrado, si existe
     */
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorUsuarioInstitucional(String usuario);
    void actualizarPassword(Long idUsuario, String passwordCifrada);

    /**
     * Persiste un usuario sin devolver su identificador.
     *
     * @param usuario usuario a registrar
     */
    void guardar(Usuario usuario);

    /**
     * Persiste un usuario y retorna la llave generada.
     *
     * @param usuario usuario a registrar
     * @return identificador generado por la base de datos
     */
    Long guardarRetornandoId(Usuario usuario);

    /**
     * Verifica si ya existe un usuario con el email indicado.
     *
     * @param email correo a validar
     * @return {@code true} si el email ya esta registrado
     */
    boolean existeEmail(String email);

    /**
     * Actualiza los datos de autenticacion compartidos con perfiles del sistema.
     *
     * @param id identificador del usuario asociado
     * @param usuario datos actualizados
     */
    void actualizarDatosBasicos(Long id, Usuario usuario);
}
