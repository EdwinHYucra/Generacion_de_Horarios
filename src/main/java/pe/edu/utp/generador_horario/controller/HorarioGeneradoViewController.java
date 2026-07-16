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
import pe.edu.utp.generador_horario.dao.AulaDAO;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.dto.HorariosDocenteGrupoDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;
import pe.edu.utp.generador_horario.entidad.Docente;

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
    private final AulaDAO aulaDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final DisponibilidadDocenteDAO disponibilidadDocenteDAO;

    public HorarioGeneradoViewController(
            HorarioGeneradoService horarioGeneradoService,
            DocenteDAO docenteDAO,
            AulaDAO aulaDAO,
            CicloAcademicoDAO cicloAcademicoDAO,
            DisponibilidadDocenteDAO disponibilidadDocenteDAO) {
        this.horarioGeneradoService = horarioGeneradoService;
        this.docenteDAO = docenteDAO;
        this.aulaDAO = aulaDAO;
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.disponibilidadDocenteDAO = disponibilidadDocenteDAO;
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
        horariosPorDocente.replaceAll((idDocente, opciones) -> ultimasOpciones(opciones));

        List<Docente> docentes = docenteDAO.findAll();
        Map<Long, Docente> docentesPorId = docentes.stream()
                .collect(Collectors.toMap(Docente::getIdDocente, docente -> docente));
        model.addAttribute("docentes", docentes);
        model.addAttribute("horarios", horarios);
        model.addAttribute("horariosAgrupados", construirGrupos(horariosPorDocente, docentesPorId));
        model.addAttribute("opcionesPorDocente", construirOpcionesPorDocente(horariosPorDocente));
        model.addAttribute("detalleHorario", idHorario == null
                ? null
                : horarioGeneradoService.listarDetalles(idHorario));
        model.addAttribute("horarioSeleccionado", idHorario);
        model.addAttribute("moduloActivo", "horarios");
        return "horarios/index";
    }

    /**
     * Conserva solamente la generación más reciente de cada opción. De esta
     * manera el modal no mezcla las opciones 1, 2 y 3 de ejecuciones anteriores.
     */
    private List<HorarioGeneradoResumenDTO> ultimasOpciones(List<HorarioGeneradoResumenDTO> historico) {
        Map<Integer, HorarioGeneradoResumenDTO> ultimaPorNumero = new LinkedHashMap<>();
        historico.stream()
                .sorted(Comparator.comparing(HorarioGeneradoResumenDTO::getIdHorario))
                .forEach(opcion -> ultimaPorNumero.put(opcion.getOpcion(), opcion));
        return ultimaPorNumero.values().stream()
                .sorted(Comparator.comparing(HorarioGeneradoResumenDTO::getOpcion))
                .limit(3)
                .toList();
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

    @PostMapping("/habilitar-seleccion/{idDocente}")
    public String habilitarSeleccion(
            @PathVariable Long idDocente,
            RedirectAttributes redirectAttributes) {
        try {
            horarioGeneradoService.habilitarNuevaSeleccion(idDocente);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Nueva seleccion habilitada. El docente puede registrar otra vez su disponibilidad y cursos.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo habilitar la nueva seleccion. Intente nuevamente.");
        }
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

    @PostMapping("/rechazar/{id}")
    public String rechazar(@PathVariable("id") Long idHorario, RedirectAttributes redirectAttributes) {
        try {
            horarioGeneradoService.rechazarPorAdministrador(idHorario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Propuesta rechazada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/administrador/horarios";
    }

    @GetMapping("/editar/{id}")
    public String editarHorario(@PathVariable("id") Long idHorario, Model model, RedirectAttributes redirectAttributes) {
        HorarioGeneradoResumenDTO resumen = horarioGeneradoService.listarResumenes().stream()
                .filter(item -> idHorario.equals(item.getIdHorario())).findFirst().orElse(null);
        if (resumen == null || !("EN_REVISION".equals(resumen.getEstado()) || "APROBADA_DOCENTE".equals(resumen.getEstado()))) {
            redirectAttributes.addFlashAttribute("mensajeError", "El horario no está disponible para edición.");
            return "redirect:/administrador/horarios";
        }
        Long cicloId = cicloAcademicoDAO.findIdActivo().orElse(null);
        model.addAttribute("resumen", resumen);
        model.addAttribute("bloques", horarioGeneradoService.listarDetalles(idHorario));
        model.addAttribute("aulas", aulaDAO.findByEstadoTrue());
        model.addAttribute("disponibilidad", cicloId == null ? List.of()
                : disponibilidadDocenteDAO.findByDocenteIdAndCicloId(resumen.getIdDocente(), cicloId));
        model.addAttribute("dias", List.of("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"));
        model.addAttribute("moduloActivo", "horarios");
        return "horarios/editar";
    }

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable("id") Long idHorario,
            @RequestParam("idCurso") List<Long> idsCurso,
            @RequestParam("curso") List<String> cursos,
            @RequestParam("idAula") List<Long> idsAula,
            @RequestParam("dia") List<String> dias,
            @RequestParam("horaInicio") List<String> horasInicio,
            @RequestParam("horaFin") List<String> horasFin,
            RedirectAttributes redirectAttributes) {
        try {
            if (!(idsCurso.size() == cursos.size() && cursos.size() == idsAula.size()
                    && idsAula.size() == dias.size() && dias.size() == horasInicio.size()
                    && horasInicio.size() == horasFin.size())) {
                throw new IllegalArgumentException("Los bloques enviados están incompletos.");
            }
            List<HorarioDetalleDTO> bloques = new java.util.ArrayList<>();
            for (int i = 0; i < idsCurso.size(); i++) {
                HorarioDetalleDTO bloque = new HorarioDetalleDTO();
                bloque.setIdCurso(idsCurso.get(i)); bloque.setCurso(cursos.get(i)); bloque.setIdAula(idsAula.get(i));
                bloque.setDia(dias.get(i)); bloque.setHoraInicio(horasInicio.get(i)); bloque.setHoraFin(horasFin.get(i));
                bloques.add(bloque);
            }
            horarioGeneradoService.editarYAprobar(idHorario, bloques);
            redirectAttributes.addFlashAttribute("mensajeExito", "Horario corregido y aprobado correctamente.");
            return "redirect:/administrador/horarios";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/administrador/horarios/editar/" + idHorario;
        }
    }

    private List<HorariosDocenteGrupoDTO> construirGrupos(
            Map<Long, List<HorarioGeneradoResumenDTO>> horariosPorDocente,
            Map<Long, Docente> docentesPorId) {

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
                    Docente docente = docentesPorId.get(entry.getKey());
                    grupo.setCarrera(docente == null || docente.getCarrera() == null
                            ? "No asignada" : docente.getCarrera());
                    opciones.stream()
                            .filter(item -> "APROBADA_DOCENTE".equals(item.getEstado())
                                    || "APROBADO".equals(item.getEstado()))
                            .findFirst()
                            .ifPresent(elegida -> {
                                grupo.setOpcionElegida(elegida.getOpcion());
                                grupo.setIdHorarioElegido(elegida.getIdHorario());
                            });
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
