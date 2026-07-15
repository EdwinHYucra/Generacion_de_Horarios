package pe.edu.utp.generador_horario.controller;

import pe.edu.utp.generador_horario.entidad.Carrera;
import pe.edu.utp.generador_horario.service.interfaces.CarreraService;
import pe.edu.utp.generador_horario.entidad.CarreraCurso;
import pe.edu.utp.generador_horario.service.interfaces.CarreraCursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador MVC para la gestion de carreras.
 *
 * <p>Permite listar, registrar, editar y desactivar carreras academicas.</p>
 *
 * @author Edwin
 */
@Controller
@RequestMapping("/administrador/carreras")
public class CarreraViewController {

    private final CarreraService carreraService;
    private final CarreraCursoService carreraCursoService;

    public CarreraViewController(CarreraService carreraService,
                                 CarreraCursoService carreraCursoService) {
        this.carreraService = carreraService;
        this.carreraCursoService = carreraCursoService;
    }

    /**
     * Muestra carreras activas y formulario del modulo.
     *
     * @param model modelo usado por la vista
     * @return plantilla de gestion de carreras
     */
    @GetMapping
    public String listarCarreras(Model model) {
        if (!model.containsAttribute("carrera")) {
            Carrera carrera = new Carrera();
            carrera.setEstado(true);
            model.addAttribute("carrera", carrera);
        }

        cargarDatosVista(model, model.containsAttribute("modoEdicion"));

        return "carreras/index";
    }

    /**
     * Procesa el guardado de una carrera.
     *
     * @param carrera datos enviados desde el formulario
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de carreras
     */
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

        return "redirect:/administrador/carreras";
    }

    /**
     * Carga una carrera existente para editarla.
     *
     * @param id identificador de la carrera
     * @param model modelo usado por la vista
     * @return plantilla de gestion de carreras en modo edicion
     */
    @GetMapping("/editar/{id}")
    public String editarCarrera(@PathVariable("id") Long id, Model model) {
        Carrera carrera = carreraService.obtenerPorId(id);

        model.addAttribute("carrera", carrera);
        cargarDatosVista(model, true);

        return "carreras/index";
    }

    /**
     * Desactiva logicamente una carrera.
     *
     * @param id identificador de la carrera
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de carreras
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarCarrera(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        carreraService.desactivarCarrera(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Carrera desactivada correctamente.");
        return "redirect:/administrador/carreras";
    }

    /** Carga carreras y asignaciones reales para el formulario y la tabla. */
    private void cargarDatosVista(Model model, boolean modoEdicion) {
        List<CarreraCurso> asignaciones = carreraCursoService.listarAsignaciones();
        Map<Long, List<CarreraCurso>> asignacionesPorCarrera = asignaciones.stream()
                .collect(Collectors.groupingBy(item -> item.getCarrera().getIdCarrera()));

        Carrera carreraActual = (Carrera) model.getAttribute("carrera");
        List<CarreraCurso> asignacionesActuales = carreraActual != null && carreraActual.getIdCarrera() != null
                ? asignacionesPorCarrera.getOrDefault(carreraActual.getIdCarrera(), List.of())
                : List.of();

        model.addAttribute("carreras", carreraService.listarCarreras());
        model.addAttribute("asignacionesPorCarrera", asignacionesPorCarrera);
        model.addAttribute("asignacionesCarreraActual", asignacionesActuales);
        model.addAttribute("modoEdicion", modoEdicion);
        model.addAttribute("moduloActivo", "carreras");
    }

}


