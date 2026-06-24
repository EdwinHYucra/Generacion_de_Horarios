package pe.edu.utp.generador_horario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;

/**
 * Controlador administrativo para generar, revisar y aprobar horarios.
 */
@Controller
@RequestMapping("/administrador/horarios")
public class HorarioGeneradoViewController {

    private final HorarioGeneradoService horarioGeneradoService;
    private final DocenteDAO docenteDAO;

    public HorarioGeneradoViewController(
            HorarioGeneradoService horarioGeneradoService,
            DocenteDAO docenteDAO) {
        this.horarioGeneradoService = horarioGeneradoService;
        this.docenteDAO = docenteDAO;
    }

    @GetMapping
    public String listar(
            @RequestParam(value = "horario", required = false) Long idHorario,
            Model model) {
        model.addAttribute("docentes", docenteDAO.findAll());
        model.addAttribute("horarios", horarioGeneradoService.listarResumenes());
        model.addAttribute("detalleHorario", idHorario == null
                ? null
                : horarioGeneradoService.listarDetalles(idHorario));
        model.addAttribute("horarioSeleccionado", idHorario);
        model.addAttribute("moduloActivo", "horarios");
        return "horarios/index";
    }

    @PostMapping("/generar")
    public String generarDocente(
            @RequestParam("idDocente") Long idDocente,
            RedirectAttributes redirectAttributes) {
        int generadas = horarioGeneradoService.generarParaDocente(idDocente);
        redirectAttributes.addFlashAttribute(
                generadas > 0 ? "mensajeExito" : "mensajeError",
                generadas > 0
                        ? "Se generaron " + generadas + " opciones de horario."
                        : "No se pudo generar horario: revise cursos, disponibilidad y restricciones.");
        return "redirect:/administrador/horarios";
    }

    @PostMapping("/generar-todos")
    public String generarTodos(RedirectAttributes redirectAttributes) {
        int generadas = horarioGeneradoService.generarParaTodos();
        redirectAttributes.addFlashAttribute(
                generadas > 0 ? "mensajeExito" : "mensajeError",
                generadas > 0
                        ? "Generacion finalizada. Opciones guardadas: " + generadas + "."
                        : "No se generaron opciones. Revise datos academicos y disponibilidad.");
        return "redirect:/administrador/horarios";
    }

    @PostMapping("/aprobar/{id}")
    public String aprobar(
            @PathVariable("id") Long idHorario,
            RedirectAttributes redirectAttributes) {
        horarioGeneradoService.aprobar(idHorario);
        redirectAttributes.addFlashAttribute("mensajeExito", "Horario aprobado correctamente.");
        return "redirect:/administrador/horarios?horario=" + idHorario;
    }
}
