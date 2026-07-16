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
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.SeleccionCursosRequestDTO;
import pe.edu.utp.generador_horario.entidad.Curso;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import pe.edu.utp.generador_horario.service.interfaces.SeleccionCursosAsyncService;

import java.util.List;

/**
 * Controlador del modulo docente para seleccionar cursos disponibles.
 */
@Controller
@RequestMapping("/docente/cursos")
public class CursoDocenteController {

        private static final Logger LOGGER = LoggerFactory.getLogger(CursoDocenteController.class);
        private static final int MAX_HORAS_SEMANALES = 40;
        private static final double PUNTAJE_MINIMO_CURSO_DISPONIBLE = 7.0;

        private final CursoService cursoService;
        private final CicloAcademicoDAO cicloAcademicoDAO;
        private final UsuarioDAO usuarioDAO;
        private final DocenteDAO docenteDAO;
        private final DocenteCursoDAO docenteCursoDAO;
        private final SeleccionCursosAsyncService seleccionCursosAsyncService;

        public CursoDocenteController(
                        CursoService cursoService,
                        CicloAcademicoDAO cicloAcademicoDAO,
                        UsuarioDAO usuarioDAO,
                        DocenteDAO docenteDAO,
                        DocenteCursoDAO docenteCursoDAO,
                        SeleccionCursosAsyncService seleccionCursosAsyncService) {

                this.cursoService = cursoService;
                this.cicloAcademicoDAO = cicloAcademicoDAO;
                this.usuarioDAO = usuarioDAO;
                this.docenteDAO = docenteDAO;
                this.docenteCursoDAO = docenteCursoDAO;
                this.seleccionCursosAsyncService = seleccionCursosAsyncService;
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
                String palabraCarrera = obtenerPalabraCarrera(docente);

                Long cicloAnteriorId = cicloAcademicoDAO.findIdAnteriorAlActivo().orElse(null);

                model.addAttribute("cursosCarrera", cicloAnteriorId == null
                                ? cursoService.listarCursosDeCarreraPorCarrera(palabraCarrera)
                                : cursoService.listarCursosDeCarreraPorCarreraFiltrandoEvaluacion(
                                                palabraCarrera,
                                                docente.getIdDocente(),
                                                cicloAnteriorId,
                                                PUNTAJE_MINIMO_CURSO_DISPONIBLE));
                model.addAttribute("cursosGenerales", listarCursosGeneralesDisponibles(
                                palabraCarrera,
                                docente.getIdDocente(),
                                cicloAnteriorId));
                List<Long> cursosSeleccionados = docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(
                                docente.getIdDocente(),
                                cicloActivoId);
                model.addAttribute("cursosSeleccionados", cursosSeleccionados);
                model.addAttribute("cursosBloqueados", !cursosSeleccionados.isEmpty());
                model.addAttribute("maxHorasSemanales", MAX_HORAS_SEMANALES);

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
                if (!docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId).isEmpty()) {
                        return ResponseEntity.badRequest()
                                        .body("La seleccion de cursos ya fue confirmada y no puede modificarse.");
                }

                boolean confirmacionProgramada = seleccionCursosAsyncService.programarConfirmacion(
                                docente.getIdDocente(),
                                request.getCursosSeleccionados());

                LOGGER.info("Confirmacion de cursos recibida. docenteId={}, cicloId={}, programada={}",
                                docente.getIdDocente(), cicloActivoId, confirmacionProgramada);

                return ResponseEntity.accepted().body(confirmacionProgramada
                                ? "Seleccion recibida. Estamos generando opciones de horario."
                                : "La seleccion ya se encuentra en proceso.");
        }

        private Long obtenerCicloActivoId() {
                return cicloAcademicoDAO.findIdActivo()
                                .orElseThrow(() -> new IllegalStateException("No existe un ciclo academico activo."));
        }

        private String obtenerPalabraCarrera(Docente docente) {
                String carreraDocente = docente.getCarrera();
                if (carreraDocente != null && carreraDocente.toLowerCase().contains("sistemas")) {
                        return "Sistemas";
                }
                if (carreraDocente != null && carreraDocente.toLowerCase().contains("civil")) {
                        return "Civil";
                }
                return carreraDocente;
        }

        private List<Curso> listarCursosGeneralesDisponibles(
                        String palabraCarrera,
                        Long idDocente,
                        Long cicloAnteriorId) {
                return cicloAnteriorId == null
                                ? cursoService.listarCursosGeneralesPorCarrera(palabraCarrera)
                                : cursoService.listarCursosGeneralesPorCarreraFiltrandoEvaluacion(
                                                palabraCarrera,
                                                idDocente,
                                                cicloAnteriorId,
                                                PUNTAJE_MINIMO_CURSO_DISPONIBLE);
        }

}
