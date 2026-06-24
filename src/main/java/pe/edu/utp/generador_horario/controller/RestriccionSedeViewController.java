package pe.edu.utp.generador_horario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.entidad.RestriccionSede;
import pe.edu.utp.generador_horario.entidad.Sede;
import pe.edu.utp.generador_horario.service.interfaces.RestriccionSedeService;
import pe.edu.utp.generador_horario.service.interfaces.SedeService;

/**
 * Controlador MVC para configurar reglas de traslado entre sedes.
 *
 * <p>Estas reglas son consumidas por el algoritmo de generacion de horarios
 * para evitar clases consecutivas sin tiempo suficiente de traslado.</p>
 */
@Controller
@RequestMapping("/administrador/restricciones/sedes")
public class RestriccionSedeViewController {

    private final RestriccionSedeService restriccionSedeService;
    private final SedeService sedeService;

    public RestriccionSedeViewController(
            RestriccionSedeService restriccionSedeService,
            SedeService sedeService) {
        this.restriccionSedeService = restriccionSedeService;
        this.sedeService = sedeService;
    }

    /**
     * Muestra el formulario y la tabla de reglas configuradas.
     *
     * @param model modelo de la vista
     * @return plantilla de restricciones entre sedes
     */
    @GetMapping
    public String listar(Model model) {
        if (!model.containsAttribute("restriccionSede")) {
            model.addAttribute("restriccionSede", nuevaRestriccion());
        }

        cargarCatalogos(model);
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        return "restricciones/sedes";
    }

    /**
     * Guarda una regla de traslado.
     *
     * @param restriccionSede datos del formulario
     * @param redirectAttributes mensajes flash
     * @return redireccion al modulo
     */
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("restriccionSede") RestriccionSede restriccionSede,
            RedirectAttributes redirectAttributes) {
        try {
            restriccionSedeService.guardar(restriccionSede);
            redirectAttributes.addFlashAttribute("mensajeExito", "Regla de traslado guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("restriccionSede", restriccionSede);
            redirectAttributes.addFlashAttribute("modoEdicion", restriccionSede.getIdRestriccion() != null);
        }

        return "redirect:/administrador/restricciones/sedes";
    }

    /**
     * Carga una regla para editarla.
     *
     * @param id identificador de la regla
     * @param model modelo de la vista
     * @return plantilla en modo edicion
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        model.addAttribute("restriccionSede", restriccionSedeService.obtenerPorId(id));
        model.addAttribute("modoEdicion", true);
        cargarCatalogos(model);
        return "restricciones/sedes";
    }

    /**
     * Elimina una regla de traslado.
     *
     * @param id identificador de la regla
     * @param redirectAttributes mensajes flash
     * @return redireccion al modulo
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {
        restriccionSedeService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Regla de traslado eliminada correctamente.");
        return "redirect:/administrador/restricciones/sedes";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("restricciones", restriccionSedeService.listarRestricciones());
        model.addAttribute("sedes", sedeService.listarSedes());
        model.addAttribute("moduloActivo", "restricciones");
    }

    private RestriccionSede nuevaRestriccion() {
        RestriccionSede restriccion = new RestriccionSede();
        restriccion.setSedeOrigen(new Sede());
        restriccion.setSedeDestino(new Sede());
        return restriccion;
    }
}
