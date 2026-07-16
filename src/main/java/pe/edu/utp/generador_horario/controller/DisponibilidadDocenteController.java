package pe.edu.utp.generador_horario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.BloqueDisponibilidadDTO;
import pe.edu.utp.generador_horario.dto.DisponibilidadRequestDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.DisponibilidadDocenteService;

import java.util.List;

@Controller
@RequestMapping("/docente/disponibilidad")
public class DisponibilidadDocenteController {

    private final DisponibilidadDocenteService disponibilidadService;
    private final UsuarioDAO usuarioDAO;

    public DisponibilidadDocenteController(
            DisponibilidadDocenteService disponibilidadService,
            UsuarioDAO usuarioDAO) {
        this.disponibilidadService = disponibilidadService;
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping
    public String mostrarDisponibilidad(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "disponibilidad");
        model.addAttribute("disponibilidadBloqueada",
                !disponibilidadService.listarPorEmail(authentication.getName()).isEmpty());

        return "docente/disponibilidad";
    }

    @GetMapping("/listar")
    @ResponseBody
    public List<BloqueDisponibilidadDTO> listar(Authentication authentication) {
        return disponibilidadService.listarPorEmail(authentication.getName());
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<String> guardar(
            @RequestBody DisponibilidadRequestDTO request,
            Authentication authentication) {
        try {
            disponibilidadService.guardarPorEmail(
                    authentication.getName(),
                    request.getBloques());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(
                "Disponibilidad guardada correctamente.");
    }

}
