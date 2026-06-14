package pe.edu.utp.generador_horario.controller;

import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.service.interfaces.DocenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para la gestion de docentes.
 *
 * <p>Permite listar, registrar, editar y desactivar docentes desde la vista
 * administrativa del modulo.</p>
 *
 * @author Edwin
 */
@Controller
@RequestMapping("/administrador/docentes")
public class DocenteViewController {

    private final DocenteService docenteService;

    public DocenteViewController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    /**
     * Muestra docentes registrados y el formulario del modulo.
     *
     * @param model modelo usado por la vista
     * @return plantilla de gestion de docentes
     */
    @GetMapping
    public String listarDocentes(Model model) {
        if (!model.containsAttribute("docente")) {
            Docente docente = new Docente();
            docente.setEstado(true);
            model.addAttribute("docente", docente);
        }

        model.addAttribute("docentes", docenteService.listarDocentes());
        model.addAttribute("modoEdicion", model.containsAttribute("modoEdicion"));
        model.addAttribute("moduloActivo", "docentes");

        return "docentes/index";
    }

    /**
     * Procesa el guardado de un docente.
     *
     * @param docente datos enviados desde el formulario
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de docentes
     */
    @PostMapping("/guardar")
    public String guardarDocente(@ModelAttribute("docente") Docente docente,
                                 RedirectAttributes redirectAttributes) {
        try {
            boolean esNuevo = docente.getIdDocente() == null;
            docenteService.guardarDocente(docente);

            String mensaje = esNuevo
                    ? "Docente registrado correctamente."
                    : "Docente actualizado correctamente.";

            redirectAttributes.addFlashAttribute("mensajeExito", mensaje);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            redirectAttributes.addFlashAttribute("docente", docente);
            redirectAttributes.addFlashAttribute("modoEdicion", docente.getIdDocente() != null);
        }

        return "redirect:/administrador/docentes";
    }

    /**
     * Carga un docente existente para editarlo.
     *
     * @param id identificador del docente
     * @param model modelo usado por la vista
     * @return plantilla de gestion de docentes en modo edicion
     */
    @GetMapping("/editar/{id}")
    public String editarDocente(@PathVariable("id") Long id, Model model) {
        Docente docente = docenteService.obtenerPorId(id);

        model.addAttribute("docente", docente);
        model.addAttribute("docentes", docenteService.listarDocentes());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("moduloActivo", "docentes");

        return "docentes/index";
    }

    /**
     * Desactiva logicamente un docente.
     *
     * @param id identificador del docente
     * @param redirectAttributes atributos flash para mensajes de resultado
     * @return redireccion al modulo de docentes
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarDocente(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        docenteService.desactivarDocente(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Docente desactivado correctamente.");

        return "redirect:/administrador/docentes";
    }
}


