package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Aula;
import com.utp.generacionhorarios.service.interfaces.AulaService;
import com.utp.generacionhorarios.service.interfaces.SedeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aulas")
public class AulaViewController {

    private final AulaService aulaService;
    private final SedeService sedeService;

    public AulaViewController(AulaService aulaService, SedeService sedeService) {
        this.aulaService = aulaService;
        this.sedeService = sedeService;
    }

    @GetMapping
    public String listarAulas(Model model) {
        if (!model.containsAttribute("aula")) {
            Aula aula = new Aula();
            aula.setEstado(true);
            model.addAttribute("aula", aula);
        }

        model.addAttribute("aulas", aulaService.listarAulas());
        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "aulas");

        return "aulas/index";
    }

    @PostMapping("/guardar")
    public String guardarAula(@ModelAttribute("aula") Aula aula,
                              RedirectAttributes redirectAttributes) {
        try {
            aulaService.guardarAula(aula);
            redirectAttributes.addFlashAttribute("mensajeExito", "Aula guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("aula", aula);
            redirectAttributes.addFlashAttribute("modoEdicion", aula.getIdAula() != null);
        }

        return "redirect:/aulas";
    }

    @GetMapping("/editar/{id}")
    public String editarAula(@PathVariable("id") Long id, Model model) {
        Aula aula = aulaService.obtenerPorId(id);

        model.addAttribute("aula", aula);
        model.addAttribute("aulas", aulaService.listarAulas());
        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "aulas");

        return "aulas/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarAula(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes) {
        aulaService.desactivarAula(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Aula desactivada correctamente.");
        return "redirect:/aulas";
    }
}