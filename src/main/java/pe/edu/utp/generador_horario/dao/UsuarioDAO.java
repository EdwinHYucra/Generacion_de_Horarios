package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.Usuario;
import java.util.Optional;

public interface UsuarioDAO {

    Optional<Usuario> buscarPorEmail(String email);
    void guardar(Usuario usuario);
    Long guardarRetornandoId(Usuario usuario);
    boolean existeEmail(String email);
}
