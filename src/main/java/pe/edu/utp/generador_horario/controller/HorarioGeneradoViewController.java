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
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.dto.HorariosDocenteGrupoDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<HorarioGeneradoResumenDTO> horarios = horarioGeneradoService.listarResumenes();
        Map<Long, List<HorarioGeneradoResumenDTO>> horariosPorDocente = horarios.stream()
                .collect(Collectors.groupingBy(
                        HorarioGeneradoResumenDTO::getIdDocente,
                        LinkedHashMap::new,
                        Collectors.toList()));

        model.addAttribute("docentes", docenteDAO.findAll());
        model.addAttribute("horarios", horarios);
        model.addAttribute("horariosAgrupados", construirGrupos(horariosPorDocente));
        model.addAttribute("opcionesPorDocente", construirOpcionesPorDocente(horariosPorDocente));
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
        try {
            horarioGeneradoService.aprobar(idHorario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Horario aprobado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/administrador/horarios?horario=" + idHorario;
    }

    private List<HorariosDocenteGrupoDTO> construirGrupos(
            Map<Long, List<HorarioGeneradoResumenDTO>> horariosPorDocente) {

        return horariosPorDocente.entrySet()
                .stream()
                .map(entry -> {
                    List<HorarioGeneradoResumenDTO> opciones = entry.getValue();
                    HorarioGeneradoResumenDTO primera = opciones.get(0);

                    HorariosDocenteGrupoDTO grupo = new HorariosDocenteGrupoDTO();
                    grupo.setIdDocente(entry.getKey());
                    grupo.setDocente(primera.getDocente());
                    grupo.setCantidadOpciones(opciones.size());
                    grupo.setTotalBloques(opciones.stream()
                            .mapToInt(item -> item.getTotalBloques() == null ? 0 : item.getTotalBloques())
                            .sum());
                    grupo.setEstadoResumen(resumirEstado(opciones));
                    grupo.setFechaGeneracion(primera.getFechaGeneracion());
                    return grupo;
                })
                .toList();
    }

    private Map<Long, List<OpcionesHorarioDTO>> construirOpcionesPorDocente(
            Map<Long, List<HorarioGeneradoResumenDTO>> horariosPorDocente) {

        Map<Long, List<OpcionesHorarioDTO>> opcionesPorDocente = new LinkedHashMap<>();
        for (Map.Entry<Long, List<HorarioGeneradoResumenDTO>> entry : horariosPorDocente.entrySet()) {
            List<OpcionesHorarioDTO> opciones = entry.getValue()
                    .stream()
                    .sorted(Comparator.comparing(HorarioGeneradoResumenDTO::getOpcion))
                    .map(resumen -> {
                        OpcionesHorarioDTO opcion = new OpcionesHorarioDTO();
                        opcion.setIdHorario(resumen.getIdHorario());
                        opcion.setOpcion(resumen.getOpcion());
                        opcion.setObservacion(resumen.getEstado());
                        opcion.setBloques(horarioGeneradoService.listarDetalles(resumen.getIdHorario()));
                        return opcion;
                    })
                    .toList();
            opcionesPorDocente.put(entry.getKey(), opciones);
        }
        return opcionesPorDocente;
    }

    private String resumirEstado(List<HorarioGeneradoResumenDTO> opciones) {
        if (opciones.stream().anyMatch(item -> "APROBADA_DOCENTE".equals(item.getEstado()))) {
            return "APROBADA_DOCENTE";
        }
        if (opciones.stream().anyMatch(item -> "APROBADO".equals(item.getEstado()))) {
            return "APROBADO";
        }
        if (opciones.stream().allMatch(item -> "DESCARTADO".equals(item.getEstado()))) {
            return "DESCARTADO";
        }
        return "PENDIENTE";
    }
}
