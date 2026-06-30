package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.SolicitudCambioHorarioService;

@Controller
public class SolicitudDocenteController {

    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final SolicitudCambioHorarioService solicitudCambioHorarioService;

    public SolicitudDocenteController(
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO,
            SolicitudCambioHorarioService solicitudCambioHorarioService) {
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.solicitudCambioHorarioService = solicitudCambioHorarioService;
    }

    @GetMapping("/docente/solicitudes")
    public String listar(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "solicitudes");
        model.addAttribute("solicitudes", solicitudCambioHorarioService.listarPorDocente(docente.getIdDocente()));
        return "docente/solicitudes";
    }
}
