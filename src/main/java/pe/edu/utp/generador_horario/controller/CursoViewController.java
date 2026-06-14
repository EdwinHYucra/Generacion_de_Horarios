package pe.edu.utp.generador_horario.controller;

import pe.edu.utp.generador_horario.entidad.Curso;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para la gestion de cursos.
 *
 * <p>Administra las pantallas de listado, registro, edicion y desactivacion.</p>
 *
 * @author Edwin
 */
@Controller
@RequestMapping("/administrador/cursos")
public class CursoViewController {

    private final CursoService cursoService;

    public CursoViewController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    /**
     * Muestra los cursos activos y el formulario del modulo.
     *
     * @param model modelo usado por la vista
     * @return plantilla de gestion de cursos
     */
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

    /**
     * Procesa el guardado de un curso.
     *
     * @param curso datos del curso enviados desde el formulario
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de cursos
     */
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

        return "redirect:/administrador/cursos";
    }

    /**
     * Carga un curso existente para editarlo.
     *
     * @param id identificador del curso
     * @param model modelo usado por la vista
     * @return plantilla de gestion de cursos en modo edicion
     */
    @GetMapping("/editar/{id}")
    public String editarCurso(@PathVariable("id") Long id, Model model) {
        Curso curso = cursoService.obtenerPorId(id);

        model.addAttribute("curso", curso);
        model.addAttribute("cursos", cursoService.listarCursos());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "cursos");

        return "cursos/index";
    }

    /**
     * Desactiva logicamente un curso.
     *
     * @param id identificador del curso
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de cursos
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        cursoService.desactivarCurso(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Curso desactivado correctamente.");
        return "redirect:/administrador/cursos";
    }
}


