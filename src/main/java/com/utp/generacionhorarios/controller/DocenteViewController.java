package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.service.DocenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/docentes")
public class DocenteViewController {

    private final DocenteService docenteService;

    public DocenteViewController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @GetMapping
    public String listarDocentes(Model model) {
        Docente docente = new Docente();
        docente.setEstado(true);

        model.addAttribute("docentes", docenteService.listarDocentes());
        model.addAttribute("docente", docente);
        model.addAttribute("modoEdicion", false);

        return "docentes/index";
    }

    @PostMapping("/guardar")
    public String guardarDocente(@ModelAttribute("docente") Docente docente) {
        docenteService.guardarDocente(docente);

        return "redirect:/docentes";
    }

    @GetMapping("/editar/{id}")
    public String editarDocente(@PathVariable("id") Long id, Model model) {
        Docente docente = docenteService.obtenerPorId(id);

        model.addAttribute("docentes", docenteService.listarDocentes());
        model.addAttribute("docente", docente);
        model.addAttribute("modoEdicion", true);

        return "docentes/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarDocente(@PathVariable("id") Long id) {
        docenteService.desactivarDocente(id);
        return "redirect:/docentes";
    }
}