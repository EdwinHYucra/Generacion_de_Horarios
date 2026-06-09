package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.service.interfaces.DocenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/docentes")
public class DocenteViewController {

    private final DocenteService docenteService;

    public DocenteViewController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @GetMapping
    public String listarDocentes(Model model) {
        if (!model.containsAttribute("docente")) {
            Docente docente = new Docente();
            docente.setEstado(true);
            model.addAttribute("docente", docente);
        }

        model.addAttribute("docentes", docenteService.listarDocentes());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "docentes");

        return "docentes/index";
    }

    @PostMapping("/guardar")
    public String guardarDocente(@ModelAttribute("docente") Docente docente,
                                 RedirectAttributes redirectAttributes) {
        try {
            docenteService.guardarDocente(docente);

            String mensaje = docente.getIdDocente() == null
                    ? "Docente registrado correctamente."
                    : "Docente actualizado correctamente.";

            redirectAttributes.addFlashAttribute("mensajeExito", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("docente", docente);
            redirectAttributes.addFlashAttribute("modoEdicion", docente.getIdDocente() != null);
        }

        return "redirect:/docentes";
    }

    @GetMapping("/editar/{id}")
    public String editarDocente(@PathVariable("id") Long id, Model model) {
        Docente docente = docenteService.obtenerPorId(id);

        model.addAttribute("docente", docente);
        model.addAttribute("docentes", docenteService.listarDocentes());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "docentes");

        return "docentes/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarDocente(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        docenteService.desactivarDocente(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Docente desactivado correctamente.");

        return "redirect:/docentes";
    }
}