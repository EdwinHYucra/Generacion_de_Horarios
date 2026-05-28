package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.dto.DisponibilidadDocenteDTO;
import com.utp.generacionhorarios.service.DisponibilidadDocenteService;
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
    public String mostrarDisponibilidad(Model model) {

        Long docenteId = 1L;

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
            Model model) {
        Long docenteId = 1L;

        try {
            disponibilidadDocenteDTO.setDocenteId(docenteId);

            disponibilidadDocenteService.guardarDisponibilidad(disponibilidadDocenteDTO);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Disponibilidad registrada correctamente.");

            return "redirect:/docente/cursos";

        } catch (IllegalArgumentException e) {

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
}