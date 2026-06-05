package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Curso;
import com.utp.generacionhorarios.service.interfaces.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cursos")
public class CursoViewController {

    private final CursoService cursoService;

    public CursoViewController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public String listarCursos(Model model) {
        if (!model.containsAttribute("curso")) {
            Curso curso = new Curso();
            curso.setEstado(true);
            model.addAttribute("curso", curso);
        }

        model.addAttribute("cursos", cursoService.listarCursos());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "cursos");

        return "cursos/index";
    }

    @PostMapping("/guardar")
    public String guardarCurso(@ModelAttribute("curso") Curso curso,
                               RedirectAttributes redirectAttributes) {
        try {
            cursoService.guardarCurso(curso);
            redirectAttributes.addFlashAttribute("mensajeExito", "Curso guardado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("curso", curso);
            redirectAttributes.addFlashAttribute("modoEdicion", curso.getIdCurso() != null);
        }

        return "redirect:/cursos";
    }

    @GetMapping("/editar/{id}")
    public String editarCurso(@PathVariable("id") Long id, Model model) {
        Curso curso = cursoService.obtenerPorId(id);

        model.addAttribute("curso", curso);
        model.addAttribute("cursos", cursoService.listarCursos());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "cursos");

        return "cursos/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        cursoService.desactivarCurso(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Curso desactivado correctamente.");
        return "redirect:/cursos";
    }
}