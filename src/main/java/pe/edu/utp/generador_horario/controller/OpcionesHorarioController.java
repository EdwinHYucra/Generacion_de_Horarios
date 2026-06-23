package pe.edu.utp.generador_horario.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.OpcionesHorarioService;

@Controller
public class OpcionesHorarioController {

    private final UsuarioDAO usuarioDAO;
    private final OpcionesHorarioService opcionesHorarioService;

    public OpcionesHorarioController(
            UsuarioDAO usuarioDAO,
            OpcionesHorarioService opcionesHorarioService) {

        this.usuarioDAO = usuarioDAO;
        this.opcionesHorarioService = opcionesHorarioService;
    }

    @GetMapping("/docente/opciones_horario")
    public String mostrarVista(
            Model model,
            Authentication authentication) {

        Usuario usuario = usuarioDAO
                .buscarPorEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        model.addAttribute(
                "nombreUsuario",
                usuario.getNombre() + " " + usuario.getApellido());

        model.addAttribute(
                "rolUsuario",
                "Docente");

        model.addAttribute(
                "diasSemana",
                List.of(
                        "Lunes",
                        "Martes",
                        "Miércoles",
                        "Jueves",
                        "Viernes"));

        model.addAttribute(
                "bloquesHora",
                List.of(
                        "07:00",
                        "09:00",
                        "11:00",
                        "13:00"));

        List<OpcionesHorarioDTO> opciones =
                opcionesHorarioService.generarHorarios(usuario.getId());

        model.addAttribute(
                "opcionesHorario",
                opciones);

        return "docente/opciones_horario";
    }
}