package pe.edu.utp.generador_horario.controller;

import pe.edu.utp.generador_horario.entidad.Aula;
import pe.edu.utp.generador_horario.service.interfaces.AulaService;
import pe.edu.utp.generador_horario.service.interfaces.SedeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para la gestion de aulas.
 *
 * <p>Atiende el listado, registro, edicion y desactivacion logica de aulas.</p>
 *
 * @author Edwin
 */
@Controller
@RequestMapping("/administrador/aulas")
public class AulaViewController {

    private final AulaService aulaService;
    private final SedeService sedeService;

    public AulaViewController(AulaService aulaService, SedeService sedeService) {
        this.aulaService = aulaService;
        this.sedeService = sedeService;
    }

    /**
     * Muestra el listado de aulas y el formulario de registro o edicion.
     *
     * @param model modelo usado por la vista
     * @return plantilla de gestion de aulas
     */
    @GetMapping
    public String listarAulas(Model model) {
        if (!model.containsAttribute("aula")) {
            Aula aula = new Aula();
            aula.setEstado(true);
            model.addAttribute("aula", aula);
        }

        model.addAttribute("aulas", aulaService.listarAulas());
        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "aulas");

        return "aulas/index";
    }

    /**
     * Procesa el guardado de un aula.
     *
     * @param aula datos enviados desde el formulario
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de aulas
     */
    @PostMapping("/guardar")
    public String guardarAula(@ModelAttribute("aula") Aula aula,
                              RedirectAttributes redirectAttributes) {
        try {
            aulaService.guardarAula(aula);
            redirectAttributes.addFlashAttribute("mensajeExito", "Aula guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("aula", aula);
            redirectAttributes.addFlashAttribute("modoEdicion", aula.getIdAula() != null);
        }

        return "redirect:/administrador/aulas";
    }

    /**
     * Carga un aula existente para editarla.
     *
     * @param id identificador del aula
     * @param model modelo usado por la vista
     * @return plantilla de gestion de aulas en modo edicion
     */
    @GetMapping("/editar/{id}")
    public String editarAula(@PathVariable("id") Long id, Model model) {
        Aula aula = aulaService.obtenerPorId(id);

        model.addAttribute("aula", aula);
        model.addAttribute("aulas", aulaService.listarAulas());
        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "aulas");

        return "aulas/index";
    }

    /**
     * Desactiva logicamente un aula.
     *
     * @param id identificador del aula
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de aulas
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarAula(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes) {
        aulaService.desactivarAula(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Aula desactivada correctamente.");
        return "redirect:/administrador/aulas";
    }
}


