package pe.edu.utp.generador_horario.controller;

import pe.edu.utp.generador_horario.entidad.CarreraCurso;
import pe.edu.utp.generador_horario.service.interfaces.CarreraCursoService;
import pe.edu.utp.generador_horario.service.interfaces.CarreraService;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para asignar cursos a carreras.
 *
 * <p>Coordina el listado, registro, edicion y desactivacion de relaciones
 * entre carreras academicas y cursos.</p>
 *
 * @author Edwin
 */
@Controller
@RequestMapping("/administrador/carrera-cursos")
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

    /**
     * Muestra las asignaciones activas y los catalogos necesarios.
     *
     * @param model modelo usado por la vista
     * @return plantilla de asignacion curso-carrera
     */
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

    /**
     * Procesa el guardado de una asignacion carrera-curso.
     *
     * @param carreraCurso relacion enviada desde el formulario
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de asignaciones
     */
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

        return "redirect:/administrador/carrera-cursos";
    }

    /**
     * Carga una asignacion existente para editarla.
     *
     * @param id identificador de la asignacion
     * @param model modelo usado por la vista
     * @return plantilla de asignacion curso-carrera en modo edicion
     */
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

    /**
     * Desactiva logicamente una asignacion carrera-curso.
     *
     * @param id identificador de la asignacion
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de asignaciones
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarAsignacion(@PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        carreraCursoService.desactivarAsignacion(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Asignación desactivada correctamente.");
        return "redirect:/administrador/carrera-cursos";
    }
}


