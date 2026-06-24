package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dao.AulaDAO;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.CursoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;
import pe.edu.utp.generador_horario.entidad.Aula;
import pe.edu.utp.generador_horario.entidad.Curso;
import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;
import pe.edu.utp.generador_horario.service.interfaces.OpcionesHorarioService;
import pe.edu.utp.generador_horario.service.interfaces.ValidadorRestriccionesHorarioService;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Genera opciones de horario para un docente usando datos reales del ciclo activo.
 *
 * <p>Patrones aplicados: Strategy y Chain of Responsibility se aprovechan a
 * traves de {@link ValidadorRestriccionesHorarioService}. Este servicio solo
 * arma candidatas y delega las reglas de negocio al validador.</p>
 */
@Service
public class OpcionesHorarioServiceImpl implements OpcionesHorarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpcionesHorarioServiceImpl.class);
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int DURACION_BASE_MINUTOS = 120;
    private static final int DURACION_MINIMA_MINUTOS = 90;

    private final DisponibilidadDocenteDAO disponibilidadDAO;
    private final DocenteCursoDAO docenteCursoDAO;
    private final CursoDAO cursoDAO;
    private final AulaDAO aulaDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final ValidadorRestriccionesHorarioService validadorRestricciones;

    public OpcionesHorarioServiceImpl(
            DisponibilidadDocenteDAO disponibilidadDAO,
            DocenteCursoDAO docenteCursoDAO,
            CursoDAO cursoDAO,
            AulaDAO aulaDAO,
            CicloAcademicoDAO cicloAcademicoDAO,
            ValidadorRestriccionesHorarioService validadorRestricciones) {

        this.disponibilidadDAO = disponibilidadDAO;
        this.docenteCursoDAO = docenteCursoDAO;
        this.cursoDAO = cursoDAO;
        this.aulaDAO = aulaDAO;
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.validadorRestricciones = validadorRestricciones;
    }

    @Override
    public List<OpcionesHorarioDTO> generarHorarios(Long docenteId) {
        Optional<Long> cicloActivo = cicloAcademicoDAO.findIdActivo();
        if (cicloActivo.isEmpty()) {
            LOGGER.warn("No se generaron opciones: no existe ciclo activo.");
            return List.of(opcionVacia(1, "No existe un ciclo academico activo."));
        }

        List<Curso> cursos = docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(docenteId, cicloActivo.get())
                .stream()
                .map(cursoDAO::findById)
                .flatMap(Optional::stream)
                .toList();

        List<DisponibilidadDocente> disponibilidades =
                disponibilidadDAO.findByDocenteIdAndCicloId(docenteId, cicloActivo.get());
        List<Aula> aulas = aulaDAO.findByEstadoTrue();

        if (cursos.isEmpty() || disponibilidades.isEmpty() || aulas.isEmpty()) {
            return List.of(opcionVacia(
                    1,
                    "Faltan cursos, disponibilidad o aulas activas para generar horarios."));
        }

        List<OpcionesHorarioDTO> opciones = new ArrayList<>();
        opciones.add(generarOpcion(
                docenteId,
                1,
                cursos,
                disponibilidades.stream()
                        .sorted(Comparator.comparing(DisponibilidadDocente::getHoraInicio))
                        .toList(),
                aulas,
                "Horario generado priorizando bloques tempranos."));
        opciones.add(generarOpcion(
                docenteId,
                2,
                cursos,
                disponibilidades.stream()
                        .sorted(Comparator.comparing(DisponibilidadDocente::getDiaSemana)
                                .thenComparing(DisponibilidadDocente::getHoraInicio))
                        .toList(),
                aulas,
                "Horario generado distribuyendo dias disponibles."));
        opciones.add(generarOpcion(
                docenteId,
                3,
                cursos.stream()
                        .sorted(Comparator.comparing(Curso::getHorasSemanales).reversed())
                        .toList(),
                disponibilidades,
                aulas,
                "Horario generado priorizando cursos de mayor carga."));

        return opciones;
    }

    private OpcionesHorarioDTO generarOpcion(
            Long docenteId,
            Integer numeroOpcion,
            List<Curso> cursos,
            List<DisponibilidadDocente> disponibilidades,
            List<Aula> aulas,
            String observacion) {

        List<AsignacionHorarioCandidataDTO> asignaciones = new ArrayList<>();
        List<HorarioDetalleDTO> bloques = new ArrayList<>();
        List<String> cursosNoAsignados = new ArrayList<>();

        for (Curso curso : cursos) {
            Optional<AsignacionHorarioCandidataDTO> candidata =
                    buscarPrimeraCandidataValida(docenteId, curso, disponibilidades, aulas, asignaciones);

            if (candidata.isPresent()) {
                asignaciones.add(candidata.get());
                bloques.add(toDetalle(curso, candidata.get(), aulas));
            } else {
                cursosNoAsignados.add(curso.getNombre());
            }
        }

        OpcionesHorarioDTO dto = new OpcionesHorarioDTO();
        dto.setOpcion(numeroOpcion);
        dto.setBloques(bloques);
        dto.setObservacion(cursosNoAsignados.isEmpty()
                ? observacion
                : observacion + " No se asignaron: " + String.join(", ", cursosNoAsignados) + ".");
        return dto;
    }

    private Optional<AsignacionHorarioCandidataDTO> buscarPrimeraCandidataValida(
            Long docenteId,
            Curso curso,
            List<DisponibilidadDocente> disponibilidades,
            List<Aula> aulas,
            List<AsignacionHorarioCandidataDTO> asignaciones) {

        for (DisponibilidadDocente disponibilidad : disponibilidades) {
            Optional<LocalTime> horaFin = calcularHoraFin(disponibilidad);
            if (horaFin.isEmpty()) {
                continue;
            }

            for (Aula aula : aulas) {
                AsignacionHorarioCandidataDTO candidata =
                        crearCandidata(docenteId, curso, aula, disponibilidad, horaFin.get());
                ResultadoRestriccionDTO resultado =
                        validadorRestricciones.validar(candidata, asignaciones);

                if (resultado.isValido()) {
                    return Optional.of(candidata);
                }
            }
        }

        LOGGER.info("Curso no asignado por restricciones. docenteId={}, cursoId={}",
                docenteId, curso.getIdCurso());
        return Optional.empty();
    }

    private Optional<LocalTime> calcularHoraFin(DisponibilidadDocente disponibilidad) {
        long minutosDisponibles = Duration.between(
                disponibilidad.getHoraInicio(),
                disponibilidad.getHoraFin()).toMinutes();

        if (minutosDisponibles < DURACION_MINIMA_MINUTOS) {
            return Optional.empty();
        }

        int duracion = (int) Math.min(DURACION_BASE_MINUTOS, minutosDisponibles);
        return Optional.of(disponibilidad.getHoraInicio().plusMinutes(duracion));
    }

    private AsignacionHorarioCandidataDTO crearCandidata(
            Long docenteId,
            Curso curso,
            Aula aula,
            DisponibilidadDocente disponibilidad,
            LocalTime horaFin) {

        AsignacionHorarioCandidataDTO candidata = new AsignacionHorarioCandidataDTO();
        candidata.setIdDocente(docenteId);
        candidata.setIdCurso(curso.getIdCurso());
        candidata.setIdAula(aula.getIdAula());
        candidata.setIdSede(aula.getSede().getIdSede());
        candidata.setTipoCurso(normalizarTipo(curso.getTipo()));
        candidata.setTipoAula(normalizarTipo(aula.getTipo()));
        candidata.setDiaSemana(disponibilidad.getDiaSemana());
        candidata.setHoraInicio(disponibilidad.getHoraInicio());
        candidata.setHoraFin(horaFin);
        return candidata;
    }

    private HorarioDetalleDTO toDetalle(
            Curso curso,
            AsignacionHorarioCandidataDTO candidata,
            List<Aula> aulas) {

        Aula aula = aulas.stream()
                .filter(item -> item.getIdAula().equals(candidata.getIdAula()))
                .findFirst()
                .orElseThrow();

        HorarioDetalleDTO detalle = new HorarioDetalleDTO();
        detalle.setIdCurso(curso.getIdCurso());
        detalle.setIdAula(aula.getIdAula());
        detalle.setCurso(curso.getNombre());
        detalle.setAula(aula.getNombre());
        detalle.setSede(aula.getSede().getNombre());
        detalle.setDia(formatearDia(candidata.getDiaSemana()));
        detalle.setHoraInicio(candidata.getHoraInicio().format(HORA_FORMATTER));
        detalle.setHoraFin(candidata.getHoraFin().format(HORA_FORMATTER));
        return detalle;
    }

    private OpcionesHorarioDTO opcionVacia(Integer numeroOpcion, String observacion) {
        OpcionesHorarioDTO dto = new OpcionesHorarioDTO();
        dto.setOpcion(numeroOpcion);
        dto.setBloques(new ArrayList<>());
        dto.setObservacion(observacion);
        return dto;
    }

    private String normalizarTipo(String tipo) {
        return tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT);
    }

    private String formatearDia(String dia) {
        if (dia == null || dia.isBlank()) {
            return "";
        }
        String normalizado = dia.trim().toLowerCase(Locale.ROOT);
        return normalizado.substring(0, 1).toUpperCase(Locale.ROOT) + normalizado.substring(1);
    }
}
