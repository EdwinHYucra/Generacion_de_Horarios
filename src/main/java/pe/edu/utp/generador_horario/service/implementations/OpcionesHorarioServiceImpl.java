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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Genera opciones de horario para un docente usando datos reales del ciclo
 * activo.
 *
 * <p>
 * Patrones aplicados: Strategy y Chain of Responsibility se aprovechan a
 * traves de {@link ValidadorRestriccionesHorarioService}. Este servicio solo
 * arma candidatas y delega las reglas de negocio al validador.
 * </p>
 */
@Service
public class OpcionesHorarioServiceImpl implements OpcionesHorarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpcionesHorarioServiceImpl.class);
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MINUTOS_HORA_ACADEMICA = 45;
    private static final int DURACION_BASE_MINUTOS = MINUTOS_HORA_ACADEMICA * 2;
    private static final int DURACION_MINIMA_MINUTOS = MINUTOS_HORA_ACADEMICA;
    private static final int INTERVALO_INICIO_MINUTOS = 15;
    private static final int MAX_HORAS_SEMANALES = 40;
    private static final int JORNADA_MAXIMA_RECOMENDADA_MINUTOS = MINUTOS_HORA_ACADEMICA * 6;

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

        Long cicloActivoId = cicloActivo.get();
        List<Curso> cursos = docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(docenteId, cicloActivoId)
                .stream()
                .map(cursoDAO::findById)
                .flatMap(Optional::stream)
                .toList();

        List<DisponibilidadDocente> disponibilidades = disponibilidadDAO.findByDocenteIdAndCicloId(docenteId,
                cicloActivoId);
        List<Aula> aulas = aulaDAO.findByEstadoTrue();

        if (cursos.isEmpty() || disponibilidades.isEmpty() || aulas.isEmpty()) {
            return List.of(opcionVacia(
                    1,
                    "Faltan cursos, disponibilidad o aulas activas para generar horarios."));
        }

        List<Curso> cursosPriorizados = limitarCargaMaxima(
                priorizarCursosPorDisponibilidadInstitucional(cursos, cicloActivoId));

        List<OpcionesHorarioDTO> opciones = new ArrayList<>();
        opciones.add(generarOpcion(
                docenteId,
                1,
                cursosPriorizados,
                disponibilidades.stream()
                        .sorted(Comparator.comparing(DisponibilidadDocente::getHoraInicio))
                        .toList(),
                ordenarAulasParaOpcion(aulas, 1),
                "Horario generado priorizando cursos con menor cobertura docente y bloques tempranos."));
        opciones.add(generarOpcion(
                docenteId,
                2,
                cursosPriorizados,
                disponibilidades.stream()
                        .sorted(Comparator.comparing(DisponibilidadDocente::getDiaSemana)
                                .thenComparing(DisponibilidadDocente::getHoraInicio))
                        .toList(),
                ordenarAulasParaOpcion(aulas, 2),
                "Horario generado distribuyendo dias disponibles."));
        opciones.add(generarOpcion(
                docenteId,
                3,
                cursosPriorizados.stream()
                        .sorted(Comparator.comparingInt(this::horasSemanales).reversed())
                        .toList(),
                disponibilidades.stream()
                        .sorted(Comparator.comparing(DisponibilidadDocente::getHoraInicio).reversed()
                                .thenComparing(DisponibilidadDocente::getDiaSemana,
                                        Comparator.nullsLast(String::compareToIgnoreCase)))
                        .toList(),
                ordenarAulasParaOpcion(aulas, 3),
                "Horario generado priorizando cursos de mayor carga."));

        marcarOpcionesRepetidas(opciones);
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
            boolean cursoCompleto = true;
            List<AsignacionHorarioCandidataDTO> asignacionesCurso = new ArrayList<>();
            List<HorarioDetalleDTO> bloquesCurso = new ArrayList<>();

            for (Integer duracionSesion : calcularSesionesCurso(curso)) {
                Optional<AsignacionHorarioCandidataDTO> candidata = buscarMejorCandidataValida(
                        docenteId,
                        numeroOpcion,
                        curso,
                        duracionSesion,
                        disponibilidades,
                        aulas,
                        asignaciones);

                if (candidata.isPresent()) {
                    asignaciones.add(candidata.get());
                    asignacionesCurso.add(candidata.get());
                    bloquesCurso.add(toDetalle(curso, candidata.get(), aulas));
                } else {
                    cursoCompleto = false;
                    break;
                }
            }

            if (!cursoCompleto) {
                asignaciones.removeAll(asignacionesCurso);
                cursosNoAsignados.add(curso.getNombre());
            } else {
                bloques.addAll(bloquesCurso);
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

    private Optional<AsignacionHorarioCandidataDTO> buscarMejorCandidataValida(
            Long docenteId,
            Integer numeroOpcion,
            Curso curso,
            int duracionMinutos,
            List<DisponibilidadDocente> disponibilidades,
            List<Aula> aulas,
            List<AsignacionHorarioCandidataDTO> asignaciones) {

        AsignacionHorarioCandidataDTO mejorCandidata = null;
        int mejorPuntaje = Integer.MAX_VALUE;

        for (DisponibilidadDocente disponibilidad : disponibilidades) {

            for (LocalTime horaInicio : calcularIniciosPosibles(disponibilidad, duracionMinutos)) {

                LocalTime horaFin = horaInicio.plusMinutes(duracionMinutos);

                for (Aula aula : aulas) {

                    AsignacionHorarioCandidataDTO candidata = crearCandidata(
                            docenteId,
                            curso,
                            aula,
                            disponibilidad,
                            horaInicio,
                            horaFin);

                    ResultadoRestriccionDTO resultado = validadorRestricciones.validar(
                            candidata,
                            asignaciones);

                    if (resultado.isValido()) {
                        int puntaje = calcularPuntajeCandidata(candidata, asignaciones, numeroOpcion);
                        if (puntaje < mejorPuntaje) {
                            mejorPuntaje = puntaje;
                            mejorCandidata = candidata;
                        }
                    }
                }
            }
        }

        return Optional.ofNullable(mejorCandidata);
    }

    /**
     * Ordena primero los cursos con menos docentes disponibles en el ciclo.
     * Esto aplica una heuristica greedy simple: lo mas dificil de cubrir se
     * asigna antes y el resto queda para los bloques con mayor flexibilidad.
     */
    private List<Curso> priorizarCursosPorDisponibilidadInstitucional(List<Curso> cursos, Long cicloActivoId) {
        return cursos.stream()
                .sorted(Comparator
                        .comparingInt((Curso curso) -> docenteCursoDAO.countDocentesByCursoIdAndCicloId(
                                curso.getIdCurso(),
                                cicloActivoId))
                        .thenComparing(Comparator.comparingInt(this::horasSemanales).reversed())
                        .thenComparing(Curso::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    /**
     * Cambia el orden de aulas por propuesta para obtener opciones realmente
     * comparables. Sin esta variacion, el algoritmo tiende a elegir siempre la
     * primera aula valida que entrega el DAO.
     */
    private List<Aula> ordenarAulasParaOpcion(List<Aula> aulas, int numeroOpcion) {
        Comparator<Aula> porSedeYAula = Comparator
                .comparing((Aula aula) -> aula.getSede().getNombre(), Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(Aula::getNombre, Comparator.nullsLast(String::compareToIgnoreCase));

        if (numeroOpcion == 2) {
            return aulas.stream()
                    .sorted(porSedeYAula.reversed())
                    .toList();
        }

        if (numeroOpcion == 3) {
            List<Aula> ordenadas = aulas.stream()
                    .sorted(porSedeYAula)
                    .toList();
            if (ordenadas.size() <= 1) {
                return ordenadas;
            }
            int rotacion = Math.min(2, ordenadas.size() - 1);
            List<Aula> rotadas = new ArrayList<>(ordenadas);
            java.util.Collections.rotate(rotadas, -rotacion);
            return rotadas;
        }

        return aulas.stream()
                .sorted(porSedeYAula)
                .toList();
    }

    /**
     * Mantiene la carga seleccionada dentro del maximo permitido por semana.
     * La vista tambien bloquea este caso, pero el servicio conserva la regla
     * para proteger ejecuciones internas o datos historicos inconsistentes.
     */
    private List<Curso> limitarCargaMaxima(List<Curso> cursos) {
        List<Curso> cursosPermitidos = new ArrayList<>();
        int totalHoras = 0;

        for (Curso curso : cursos) {
            int horasCurso = horasSemanales(curso);
            if (totalHoras + horasCurso <= MAX_HORAS_SEMANALES) {
                cursosPermitidos.add(curso);
                totalHoras += horasCurso;
            } else {
                LOGGER.info(
                        "Curso omitido por limite de carga semanal. cursoId={}, horasCurso={}, totalActual={}",
                        curso.getIdCurso(),
                        horasCurso,
                        totalHoras);
            }
        }

        return cursosPermitidos;
    }

    /**
     * Puntua candidatas validas para evitar jornadas innecesariamente largas
     * y huecos grandes en un mismo dia. El menor puntaje es la mejor opcion.
     */
    private int calcularPuntajeCandidata(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignaciones,
            Integer numeroOpcion) {

        List<AsignacionHorarioCandidataDTO> asignacionesDelDia = asignaciones.stream()
                .filter(asignacion -> mismoDia(asignacion, candidata))
                .toList();
        int minutosDia = asignacionesDelDia.stream()
                .mapToInt(this::duracionMinutos)
                .sum();
        int duracionCandidata = duracionMinutos(candidata);
        int puntaje = asignacionesDelDia.size() * 40;

        if (minutosDia + duracionCandidata > JORNADA_MAXIMA_RECOMENDADA_MINUTOS) {
            puntaje += 1000;
        }

        int hueco = calcularMenorHuecoMinutos(candidata, asignacionesDelDia);
        if (hueco > 0) {
            puntaje += Math.min(hueco, 240);
        }

        int minutosInicio = candidata.getHoraInicio().toSecondOfDay() / 60;
        int indiceDia = indiceDia(candidata.getDiaSemana());

        if (Integer.valueOf(1).equals(numeroOpcion)) {
            puntaje += minutosInicio / 30;
        } else if (Integer.valueOf(2).equals(numeroOpcion)) {
            puntaje += asignacionesDelDia.size() * 160;
            puntaje += Math.abs(3 - indiceDia) * 12;
        } else if (Integer.valueOf(3).equals(numeroOpcion)) {
            puntaje += Math.max(0, 1440 - minutosInicio) / 8;
            puntaje += Math.max(0, 7 - indiceDia) * 10;
        }

        return puntaje;
    }

    private void marcarOpcionesRepetidas(List<OpcionesHorarioDTO> opciones) {
        Map<String, Integer> primeraOpcionPorFirma = new LinkedHashMap<>();

        for (OpcionesHorarioDTO opcion : opciones) {
            String firma = construirFirmaHorario(opcion);
            Integer primeraOpcion = primeraOpcionPorFirma.putIfAbsent(firma, opcion.getOpcion());
            if (primeraOpcion != null) {
                opcion.setObservacion(opcion.getObservacion()
                        + " Esta propuesta coincide con la opcion " + primeraOpcion
                        + " porque la disponibilidad registrada no permite una distribucion distinta.");
            }
        }
    }

    private String construirFirmaHorario(OpcionesHorarioDTO opcion) {
        return opcion.getBloques().stream()
                .sorted(Comparator
                        .comparing(HorarioDetalleDTO::getDia, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(HorarioDetalleDTO::getHoraInicio,
                                Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(HorarioDetalleDTO::getHoraFin,
                                Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(HorarioDetalleDTO::getIdCurso,
                                Comparator.nullsLast(Long::compareTo))
                        .thenComparing(HorarioDetalleDTO::getIdAula,
                                Comparator.nullsLast(Long::compareTo)))
                .map(detalle -> String.join("|",
                        valor(detalle.getDia()),
                        valor(detalle.getHoraInicio()),
                        valor(detalle.getHoraFin()),
                        String.valueOf(detalle.getIdCurso()),
                        String.valueOf(detalle.getIdAula())))
                .toList()
                .toString();
    }

    private int calcularMenorHuecoMinutos(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesDelDia) {

        return asignacionesDelDia.stream()
                .mapToInt(asignacion -> {
                    if (!asignacion.getHoraFin().isAfter(candidata.getHoraInicio())) {
                        return (int) Duration.between(asignacion.getHoraFin(), candidata.getHoraInicio()).toMinutes();
                    }
                    if (!candidata.getHoraFin().isAfter(asignacion.getHoraInicio())) {
                        return (int) Duration.between(candidata.getHoraFin(), asignacion.getHoraInicio()).toMinutes();
                    }
                    return 0;
                })
                .filter(minutos -> minutos > 0)
                .min()
                .orElse(0);
    }

    /**
     * Divide las horas academicas semanales de un curso en sesiones
     * programables. Cada hora academica equivale a 45 minutos de reloj.
     */
    private List<Integer> calcularSesionesCurso(Curso curso) {
        int minutosPendientes = Math.max(
                DURACION_MINIMA_MINUTOS,
                horasSemanales(curso) * MINUTOS_HORA_ACADEMICA);
        List<Integer> sesiones = new ArrayList<>();

        while (minutosPendientes > 0) {
            if (minutosPendientes <= DURACION_BASE_MINUTOS) {
                sesiones.add(minutosPendientes);
                break;
            }

            int remanente = minutosPendientes - DURACION_BASE_MINUTOS;
            if (remanente > 0 && remanente < DURACION_MINIMA_MINUTOS) {
                int primeraSesion = redondearIntervaloInicio(minutosPendientes / 2);
                sesiones.add(primeraSesion);
                sesiones.add(minutosPendientes - primeraSesion);
                break;
            }

            sesiones.add(DURACION_BASE_MINUTOS);
            minutosPendientes -= DURACION_BASE_MINUTOS;
        }

        return sesiones;
    }

    private int redondearIntervaloInicio(int minutos) {
        int resto = minutos % INTERVALO_INICIO_MINUTOS;
        return resto == 0 ? minutos : minutos + (INTERVALO_INICIO_MINUTOS - resto);
    }

    private List<LocalTime> calcularIniciosPosibles(DisponibilidadDocente disponibilidad, int duracionMinutos) {
        long minutosDisponibles = Duration.between(
                disponibilidad.getHoraInicio(),
                disponibilidad.getHoraFin()).toMinutes();

        if (minutosDisponibles < duracionMinutos || duracionMinutos < DURACION_MINIMA_MINUTOS) {
            return List.of();
        }

        List<LocalTime> inicios = new ArrayList<>();
        LocalTime cursor = disponibilidad.getHoraInicio();
        while (!cursor.plusMinutes(duracionMinutos).isAfter(disponibilidad.getHoraFin())) {
            inicios.add(cursor);
            cursor = cursor.plusMinutes(INTERVALO_INICIO_MINUTOS);
        }
        return inicios;
    }

    private AsignacionHorarioCandidataDTO crearCandidata(
            Long docenteId,
            Curso curso,
            Aula aula,
            DisponibilidadDocente disponibilidad,
            LocalTime horaInicio,
            LocalTime horaFin) {

        AsignacionHorarioCandidataDTO candidata = new AsignacionHorarioCandidataDTO();
        candidata.setIdDocente(docenteId);
        candidata.setIdCurso(curso.getIdCurso());
        candidata.setIdAula(aula.getIdAula());
        candidata.setIdSede(aula.getSede().getIdSede());
        candidata.setTipoCurso(normalizarTipo(curso.getTipo()));
        candidata.setTipoAula(normalizarTipo(aula.getTipo()));
        candidata.setDiaSemana(disponibilidad.getDiaSemana());
        candidata.setHoraInicio(horaInicio);
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

    private int horasSemanales(Curso curso) {
        return curso.getHorasSemanales() == null ? 0 : curso.getHorasSemanales();
    }

    private int duracionMinutos(AsignacionHorarioCandidataDTO candidata) {
        return (int) Duration.between(candidata.getHoraInicio(), candidata.getHoraFin()).toMinutes();
    }

    private boolean mismoDia(AsignacionHorarioCandidataDTO asignacion, AsignacionHorarioCandidataDTO candidata) {
        return asignacion.getDiaSemana() != null
                && asignacion.getDiaSemana().equalsIgnoreCase(candidata.getDiaSemana());
    }

    private int indiceDia(String dia) {
        if (dia == null) {
            return 7;
        }
        return switch (dia.trim().toUpperCase(Locale.ROOT)) {
            case "LUNES" -> 1;
            case "MARTES" -> 2;
            case "MIERCOLES", "MIÉRCOLES" -> 3;
            case "JUEVES" -> 4;
            case "VIERNES" -> 5;
            case "SABADO", "SÁBADO" -> 6;
            case "DOMINGO" -> 7;
            default -> 7;
        };
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }

    private String formatearDia(String dia) {
        if (dia == null || dia.isBlank()) {
            return "";
        }
        String normalizado = dia.trim().toLowerCase(Locale.ROOT);
        return normalizado.substring(0, 1).toUpperCase(Locale.ROOT) + normalizado.substring(1);
    }
}
