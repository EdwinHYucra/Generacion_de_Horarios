package pe.edu.utp.generador_horario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.SeleccionCursosRequestDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneracionAsyncService;

/**
 * Controlador del modulo docente para seleccionar cursos disponibles.
 */
@Controller
@RequestMapping("/docente/cursos")
public class CursoDocenteController {

        private static final Logger LOGGER = LoggerFactory.getLogger(CursoDocenteController.class);
        private static final int MAX_HORAS_SEMANALES = 40;
        private static final double PUNTAJE_MINIMO_CURSO_DISPONIBLE = 12.0;

        private final CursoService cursoService;
        private final CicloAcademicoDAO cicloAcademicoDAO;
        private final UsuarioDAO usuarioDAO;
        private final DocenteDAO docenteDAO;
        private final DocenteCursoDAO docenteCursoDAO;
        private final HorarioGeneracionAsyncService horarioGeneracionAsyncService;
        private final DisponibilidadDocenteDAO disponibilidadDocenteDAO;

        public CursoDocenteController(
                        CursoService cursoService,
                        CicloAcademicoDAO cicloAcademicoDAO,
                        UsuarioDAO usuarioDAO,
                        DocenteDAO docenteDAO,
                        DocenteCursoDAO docenteCursoDAO,
                        HorarioGeneracionAsyncService horarioGeneracionAsyncService,
                        DisponibilidadDocenteDAO disponibilidadDocenteDAO) {

                this.cursoService = cursoService;
                this.cicloAcademicoDAO = cicloAcademicoDAO;
                this.usuarioDAO = usuarioDAO;
                this.docenteDAO = docenteDAO;
                this.docenteCursoDAO = docenteCursoDAO;
                this.horarioGeneracionAsyncService = horarioGeneracionAsyncService;
                this.disponibilidadDocenteDAO = disponibilidadDocenteDAO;
        }

        @GetMapping
        public String mostrarCursos(Model model, Authentication authentication) {
                Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
                Long cicloActivoId = obtenerCicloActivoId();

                model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
                model.addAttribute("rolUsuario", "Docente");
                model.addAttribute("moduloActivo", "cursos");
                String carreraDocente = docente.getCarrera();

                String palabraCarrera = carreraDocente;

                if (carreraDocente != null && carreraDocente.toLowerCase().contains("sistemas")) {
                        palabraCarrera = "Sistemas";
                } else if (carreraDocente != null && carreraDocente.toLowerCase().contains("civil")) {
                        palabraCarrera = "Civil";
                }

                Long cicloAnteriorId = cicloAcademicoDAO.findIdAnteriorAlActivo().orElse(null);

                model.addAttribute("cursosCarrera", cicloAnteriorId == null
                                ? cursoService.listarCursosDeCarreraPorCarrera(palabraCarrera)
                                : cursoService.listarCursosDeCarreraPorCarreraFiltrandoEvaluacion(
                                                palabraCarrera,
                                                docente.getIdDocente(),
                                                cicloAnteriorId,
                                                PUNTAJE_MINIMO_CURSO_DISPONIBLE));
                model.addAttribute("cursosGenerales", cicloAnteriorId == null
                                ? cursoService.listarCursosGeneralesPorCarrera(palabraCarrera)
                                : cursoService.listarCursosGeneralesPorCarreraFiltrandoEvaluacion(
                                                palabraCarrera,
                                                docente.getIdDocente(),
                                                cicloAnteriorId,
                                                PUNTAJE_MINIMO_CURSO_DISPONIBLE));
                model.addAttribute("cursosSeleccionados",
                                docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(docente.getIdDocente(),
                                                cicloActivoId));
                model.addAttribute("maxHorasSemanales", MAX_HORAS_SEMANALES);
                model.addAttribute("seleccionBloqueada", seleccionCompleta(docente.getIdDocente(), cicloActivoId));

                return "docente/cursos";
        }

        @PostMapping("/guardar")
        @ResponseBody
        public ResponseEntity<String> guardarCursos(
                        @RequestBody SeleccionCursosRequestDTO request,
                        Authentication authentication) {

                Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
                Long cicloActivoId = obtenerCicloActivoId();

                if (seleccionCompleta(docente.getIdDocente(), cicloActivoId)) {
                        return ResponseEntity.status(409).body(
                                        "La disponibilidad y los cursos ya fueron confirmados para este ciclo.");
                }

                int horasSeleccionadas = request.getCursosSeleccionados() == null
                                ? 0
                                : request.getCursosSeleccionados()
                                                .stream()
                                                .map(cursoService::obtenerPorId)
                                                .mapToInt(curso -> curso.getHorasSemanales() == null ? 0
                                                                : curso.getHorasSemanales())
                                                .sum();
                if (horasSeleccionadas > MAX_HORAS_SEMANALES) {
                        return ResponseEntity.badRequest().body(
                                        "No se puede continuar: la carga seleccionada supera "
                                                        + MAX_HORAS_SEMANALES + " horas semanales.");
                }

                docenteCursoDAO.deleteByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId);

                if (request.getCursosSeleccionados() != null) {
                        for (Long idCurso : request.getCursosSeleccionados()) {
                                docenteCursoDAO.save(docente.getIdDocente(), idCurso, cicloActivoId);
                        }
                }

                int cantidad = request.getCursosSeleccionados() == null ? 0 : request.getCursosSeleccionados().size();
                LOGGER.info("Cursos seleccionados actualizados. docenteId={}, cicloId={}, cursos={}",
                                docente.getIdDocente(), cicloActivoId, cantidad);

                /*
                 * La generación automática se separó del guardado de cursos
                 * para evitar tiempos de espera elevados en la respuesta.
                 */
                horarioGeneracionAsyncService.programarGeneracion(docente.getIdDocente());
                return ResponseEntity.ok("Cursos guardados correctamente.");
        }

        private Long obtenerCicloActivoId() {
                return cicloAcademicoDAO.findIdActivo()
                                .orElseThrow(() -> new IllegalStateException("No existe un ciclo academico activo."));
        }

        private boolean seleccionCompleta(Long docenteId, Long cicloId) {
                return !docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(docenteId, cicloId).isEmpty()
                                && !disponibilidadDocenteDAO.findByDocenteIdAndCicloId(docenteId, cicloId).isEmpty();
        }
}
