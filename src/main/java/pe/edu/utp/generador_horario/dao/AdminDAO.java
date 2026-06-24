package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Contrato DAO para persistir usuarios con rol administrador.
 */
public interface AdminDAO {

    /**
     * Guarda el usuario administrador y su relacion con el SuperAdmin creador.
     *
     * @param usuario usuario con rol administrador
     * @param superAdminId identificador del SuperAdmin creador
     */
    void guardar(Usuario usuario, Long superAdminId);

    /**
     * Busca un administrador por identificador de usuario.
     */
    Optional<Usuario> buscarPorId(Long id);

    /**
     * Lista todos los administradores registrados.
     */
    List<Usuario> listarTodos();

    /**
     * Actualiza datos basicos del usuario administrador.
     */
    void actualizar(Usuario usuario);

    /**
     * Cambia el estado logico de un administrador.
     */
    void cambiarEstado(Long id, String estado);
}
