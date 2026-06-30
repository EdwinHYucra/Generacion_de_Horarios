package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.SolicitudCambioHorarioService;

@Controller
@RequestMapping("/administrador/solicitudes")
public class SolicitudAdminController {

    private final SolicitudCambioHorarioService solicitudCambioHorarioService;
    private final UsuarioDAO usuarioDAO;

    public SolicitudAdminController(
            SolicitudCambioHorarioService solicitudCambioHorarioService,
            UsuarioDAO usuarioDAO) {
        this.solicitudCambioHorarioService = solicitudCambioHorarioService;
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("solicitudes", solicitudCambioHorarioService.listarTodas());
        model.addAttribute("moduloActivo", "solicitudes");
        return "solicitudes/index";
    }

    @PostMapping("/responder")
    public String responder(
            @RequestParam("idSolicitud") Long idSolicitud,
            @RequestParam("estado") String estado,
            @RequestParam("comentarioAdministrador") String comentarioAdministrador,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        solicitudCambioHorarioService.responder(idSolicitud, usuario.getId(), estado, comentarioAdministrador);
        redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud respondida correctamente.");
        return "redirect:/administrador/solicitudes";
    }
}
