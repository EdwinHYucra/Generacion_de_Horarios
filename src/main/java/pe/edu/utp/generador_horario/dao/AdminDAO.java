package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Usuario;
import java.util.List;
import java.util.Optional;

public interface AdminDAO {

    void guardar(Usuario usuario, Long superAdminId);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarTodos();
    void actualizar(Usuario usuario);
    void cambiarEstado(Long id, String estado);
}