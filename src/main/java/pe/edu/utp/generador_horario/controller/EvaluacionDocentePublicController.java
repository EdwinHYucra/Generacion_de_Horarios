package pe.edu.utp.generador_horario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.dto.EvaluacionDocenteRequestDTO;
import pe.edu.utp.generador_horario.service.interfaces.EvaluacionDocenteService;

@Controller
public class EvaluacionDocentePublicController {

    private final EvaluacionDocenteService evaluacionDocenteService;

    public EvaluacionDocentePublicController(EvaluacionDocenteService evaluacionDocenteService) {
        this.evaluacionDocenteService = evaluacionDocenteService;
    }

    @GetMapping("/evaluacion-docente")
    public String mostrarFormulario(Model model) {
        cargarFormulario(model);
        return "evaluacion-docente/index";
    }

    @PostMapping("/evaluacion-docente")
    public String guardarEvaluacion(
            @ModelAttribute EvaluacionDocenteRequestDTO evaluacion,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            evaluacionDocenteService.guardarEvaluacion(evaluacion);
            redirectAttributes.addFlashAttribute("mensajeExito", "Evaluacion registrada correctamente.");
            return "redirect:/evaluacion-docente";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("evaluacion", evaluacion);
            cargarFormulario(model);
            return "evaluacion-docente/index";
        }
    }

    private void cargarFormulario(Model model) {
        if (!model.containsAttribute("evaluacion")) {
            model.addAttribute("evaluacion", new EvaluacionDocenteRequestDTO());
        }
        model.addAttribute("opciones", evaluacionDocenteService.listarOpcionesEvaluables());
    }
}
