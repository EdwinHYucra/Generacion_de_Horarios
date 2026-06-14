package pe.edu.utp.generador_horario.controller;

import pe.edu.utp.generador_horario.entidad.Sede;
import pe.edu.utp.generador_horario.service.interfaces.SedeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para la gestion de sedes.
 *
 * <p>Permite listar, registrar, editar y desactivar sedes institucionales.</p>
 *
 * @author Edwin
 */
@Controller
@RequestMapping("/administrador/sedes")
public class SedeViewController {

    private final SedeService sedeService;

    public SedeViewController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    /**
     * Muestra las sedes activas y el formulario del modulo.
     *
     * @param model modelo usado por la vista
     * @return plantilla de gestion de sedes
     */
    @GetMapping
    public String listarSedes(Model model) {
        if (!model.containsAttribute("sede")) {
            Sede sede = new Sede();
            sede.setEstado(true);
            model.addAttribute("sede", sede);
        }

        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "sedes");

        return "sedes/index";
    }

    /**
     * Procesa el guardado de una sede.
     *
     * @param sede datos enviados desde el formulario
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de sedes
     */
    @PostMapping("/guardar")
    public String guardarSede(@ModelAttribute("sede") Sede sede,
                              RedirectAttributes redirectAttributes) {
        try {
            sedeService.guardarSede(sede);
            redirectAttributes.addFlashAttribute("mensajeExito", "Sede guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("sede", sede);
            redirectAttributes.addFlashAttribute("modoEdicion", sede.getIdSede() != null);
        }

        return "redirect:/administrador/sedes";
    }

    /**
     * Carga una sede existente para editarla.
     *
     * @param id identificador de la sede
     * @param model modelo usado por la vista
     * @return plantilla de gestion de sedes en modo edicion
     */
    @GetMapping("/editar/{id}")
    public String editarSede(@PathVariable("id") Long id, Model model) {
        Sede sede = sedeService.obtenerPorId(id);

        model.addAttribute("sede", sede);
        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "sedes");

        return "sedes/index";
    }

    /**
     * Desactiva logicamente una sede.
     *
     * @param id identificador de la sede
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de sedes
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarSede(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes) {
        sedeService.desactivarSede(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Sede desactivada correctamente.");
        return "redirect:/administrador/sedes";
    }
}


