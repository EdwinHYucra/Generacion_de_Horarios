package pe.edu.utp.generador_horario.servicio;

import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;

import java.util.List;
import java.util.Optional;

public interface AdminServicio {

    void registrarAdmin(AdminRegistroDTO dto, Long superAdminId);
    List<Usuario> listarAdmins();
    Optional<Usuario> buscarPorId(Long id);
    void actualizarAdmin(Usuario usuario);
    void desactivarAdmin(Long id);
    void activarAdmin(Long id);
}