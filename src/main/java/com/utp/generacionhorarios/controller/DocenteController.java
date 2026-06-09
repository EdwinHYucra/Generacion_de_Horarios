package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.service.DocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/docentes")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("docentes", docenteService.listarTodos());
        return "gestion_docentes";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("docente", new Docente());
        model.addAttribute("modoEdicion", false);
        return "form_docente";
    }

    @PostMapping("/nuevo")
    public String guardar(@ModelAttribute Docente docente, RedirectAttributes ra) {
        try {
            docenteService.guardar(docente);
            ra.addFlashAttribute("mensaje", "Docente registrado correctamente.");
            ra.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            ra.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/admin/docentes";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Docente docente = docenteService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        model.addAttribute("docente", docente);
        model.addAttribute("modoEdicion", true);
        return "form_docente";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Docente datos,
                             RedirectAttributes ra) {
        try {
            docenteService.actualizar(id, datos);
            ra.addFlashAttribute("mensaje", "Docente actualizado correctamente.");
            ra.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            ra.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/admin/docentes";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes ra) {
        docenteService.desactivar(id);
        ra.addFlashAttribute("mensaje", "Docente desactivado.");
        ra.addFlashAttribute("tipo", "warning");
        return "redirect:/admin/docentes";
    }
}