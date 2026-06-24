package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Usuario;

@Controller
public class DocenteDashboardController {

    private final UsuarioDAO usuarioDAO;

    public DocenteDashboardController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping("/docente/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "dashboard");

        return "docente/dashboard";
    }
}
