package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Carrera;
import com.utp.generacionhorarios.service.interfaces.CarreraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carreras")
public class CarreraViewController {

    private final CarreraService carreraService;

    public CarreraViewController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping
    public String listarCarreras(Model model) {
        if (!model.containsAttribute("carrera")) {
            Carrera carrera = new Carrera();
            carrera.setEstado(true);
            model.addAttribute("carrera", carrera);
        }

        model.addAttribute("carreras", carreraService.listarCarreras());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "carreras");

        return "carreras/index";
    }

    @PostMapping("/guardar")
    public String guardarCarrera(@ModelAttribute("carrera") Carrera carrera,
                                 RedirectAttributes redirectAttributes) {
        try {
            carreraService.guardarCarrera(carrera);
            redirectAttributes.addFlashAttribute("mensajeExito", "Carrera guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("carrera", carrera);
            redirectAttributes.addFlashAttribute("modoEdicion", carrera.getIdCarrera() != null);
        }

        return "redirect:/carreras";
    }

    @GetMapping("/editar/{id}")
    public String editarCarrera(@PathVariable("id") Long id, Model model) {
        Carrera carrera = carreraService.obtenerPorId(id);

        model.addAttribute("carrera", carrera);
        model.addAttribute("carreras", carreraService.listarCarreras());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "carreras");

        return "carreras/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCarrera(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        carreraService.desactivarCarrera(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Carrera desactivada correctamente.");
        return "redirect:/carreras";
    }
}