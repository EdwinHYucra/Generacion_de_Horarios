package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.dto.DisponibilidadDocenteDTO;
import com.utp.generacionhorarios.service.DisponibilidadDocenteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/docente/disponibilidad")
public class DisponibilidadDocenteController {

    private final DisponibilidadDocenteService disponibilidadDocenteService;

    public DisponibilidadDocenteController(DisponibilidadDocenteService disponibilidadDocenteService) {
        this.disponibilidadDocenteService = disponibilidadDocenteService;
    }

    @GetMapping
    public String mostrarDisponibilidad(Model model, HttpSession session) {

        Long docenteId = obtenerDocenteIdDesdeSesion(session);
        String nombreDocente = obtenerNombreUsuarioAutenticado();

        model.addAttribute("nombreDocente", nombreDocente);
        model.addAttribute("rolDocente", "Docente");

        model.addAttribute("docenteId", docenteId);
        model.addAttribute("diasSemana", disponibilidadDocenteService.obtenerDiasSemana());
        model.addAttribute("bloquesHorario", disponibilidadDocenteService.obtenerBloquesHorario());
        model.addAttribute("disponibilidadRegistrada",
                disponibilidadDocenteService.obtenerDisponibilidadPorDocente(docenteId));
        model.addAttribute("disponibilidadDocenteDTO", new DisponibilidadDocenteDTO());

        return "disponibilidad";
    }

    @PostMapping("/guardar")
    public String guardarDisponibilidad(
            @ModelAttribute DisponibilidadDocenteDTO disponibilidadDocenteDTO,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {

        Long docenteId = obtenerDocenteIdDesdeSesion(session);

        try {
            disponibilidadDocenteDTO.setDocenteId(docenteId);

            disponibilidadDocenteService.guardarDisponibilidad(disponibilidadDocenteDTO);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Disponibilidad registrada correctamente.");

            return "redirect:/docente/cursos";

        } catch (IllegalArgumentException e) {

            String nombreDocente = obtenerNombreUsuarioAutenticado();

            model.addAttribute("nombreDocente", nombreDocente);
            model.addAttribute("rolDocente", "Docente");

            model.addAttribute("error", e.getMessage());
            model.addAttribute("docenteId", docenteId);
            model.addAttribute("diasSemana", disponibilidadDocenteService.obtenerDiasSemana());
            model.addAttribute("bloquesHorario", disponibilidadDocenteService.obtenerBloquesHorario());
            model.addAttribute("disponibilidadRegistrada",
                    disponibilidadDocenteService.obtenerDisponibilidadPorDocente(docenteId));
            model.addAttribute("disponibilidadDocenteDTO", disponibilidadDocenteDTO);

            return "disponibilidad";
        }
    }

    private Long obtenerDocenteIdDesdeSesion(HttpSession session) {
        Object docenteId = session.getAttribute("docenteId");

        if (docenteId != null) {
            return Long.valueOf(docenteId.toString());
        }

        return 1L;
    }

    private String obtenerNombreUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getName() == null ||
                "anonymousUser".equals(auth.getName())) {
            return "Juan Pérez";
        }

        return auth.getName();
    }
}