package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.CarreraCurso;
import com.utp.generacionhorarios.service.interfaces.CarreraCursoService;
import com.utp.generacionhorarios.service.interfaces.CarreraService;
import com.utp.generacionhorarios.service.interfaces.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrera-cursos")
public class CarreraCursoViewController {

    private final CarreraCursoService carreraCursoService;
    private final CarreraService carreraService;
    private final CursoService cursoService;

    public CarreraCursoViewController(CarreraCursoService carreraCursoService,
                                      CarreraService carreraService,
                                      CursoService cursoService) {
        this.carreraCursoService = carreraCursoService;
        this.carreraService = carreraService;
        this.cursoService = cursoService;
    }

    @GetMapping
    public String listarAsignaciones(Model model) {
        if (!model.containsAttribute("carreraCurso")) {
            CarreraCurso carreraCurso = new CarreraCurso();
            carreraCurso.setEstado(true);
            model.addAttribute("carreraCurso", carreraCurso);
        }

        model.addAttribute("asignaciones", carreraCursoService.listarAsignaciones());
        model.addAttribute("carreras", carreraService.listarCarreras());
        model.addAttribute("cursos", cursoService.listarCursos());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "carrera-cursos");

        return "carrera-cursos/index";
    }

    @PostMapping("/guardar")
    public String guardarAsignacion(@ModelAttribute("carreraCurso") CarreraCurso carreraCurso,
                                    RedirectAttributes redirectAttributes) {
        try {
            carreraCursoService.guardarAsignacion(carreraCurso);
            redirectAttributes.addFlashAttribute("mensajeExito", "Curso asignado correctamente a la carrera.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("carreraCurso", carreraCurso);
            redirectAttributes.addFlashAttribute("modoEdicion", carreraCurso.getIdCarreraCurso() != null);
        }

        return "redirect:/carrera-cursos";
    }

    @GetMapping("/editar/{id}")
    public String editarAsignacion(@PathVariable("id") Long id, Model model) {
        CarreraCurso carreraCurso = carreraCursoService.obtenerPorId(id);

        model.addAttribute("carreraCurso", carreraCurso);
        model.addAttribute("asignaciones", carreraCursoService.listarAsignaciones());
        model.addAttribute("carreras", carreraService.listarCarreras());
        model.addAttribute("cursos", cursoService.listarCursos());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "carrera-cursos");

        return "carrera-cursos/index";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarAsignacion(@PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        carreraCursoService.desactivarAsignacion(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Asignación desactivada correctamente.");
        return "redirect:/carrera-cursos";
    }
}