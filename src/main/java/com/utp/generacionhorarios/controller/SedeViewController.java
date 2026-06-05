package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Sede;
import com.utp.generacionhorarios.service.interfaces.SedeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sedes")
public class SedeViewController {

    private final SedeService sedeService;

    public SedeViewController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    @GetMapping
    public String listarSedes(Model model) {
        if (!model.containsAttribute("sede")) {
            Sede sede = new Sede();
            sede.setEstado(true);
            model.addAttribute("sede", sede);
        }

        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "sedes");

        return "sedes/index";
    }

    @PostMapping("/guardar")
    public String guardarSede(@ModelAttribute("sede") Sede sede,
                              RedirectAttributes redirectAttributes) {
        try {
            sedeService.guardarSede(sede);
            redirectAttributes.addFlashAttribute("mensajeExito", "Sede guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("sede", sede);
            redirectAttributes.addFlashAttribute("modoEdicion", sede.getIdSede() != null);
        }

        return "redirect:/sedes";
    }

    @GetMapping("/editar/{id}")
    public String editarSede(@PathVariable("id") Long id, Model model) {
        Sede sede = sedeService.obtenerPorId(id);

        model.addAttribute("sede", sede);
        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "sedes");

        return "sedes/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarSede(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes) {
        sedeService.desactivarSede(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Sede desactivada correctamente.");
        return "redirect:/sedes";
    }
}