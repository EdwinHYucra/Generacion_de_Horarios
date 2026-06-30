package pe.edu.utp.generador_horario.service.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.utp.generador_horario.dao.AulaDAO;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.CursoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;
import pe.edu.utp.generador_horario.entidad.Aula;
import pe.edu.utp.generador_horario.entidad.Curso;
import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;
import pe.edu.utp.generador_horario.entidad.Sede;
import pe.edu.utp.generador_horario.service.interfaces.ValidadorRestriccionesHorarioService;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpcionesHorarioServiceImplTest {

    private static final Long DOCENTE_ID = 7L;
    private static final Long CICLO_ID = 202601L;

    private DisponibilidadDocenteDAO disponibilidadDAO;
    private DocenteCursoDAO docenteCursoDAO;
    private CursoDAO cursoDAO;
    private AulaDAO aulaDAO;
    private CicloAcademicoDAO cicloAcademicoDAO;
    private OpcionesHorarioServiceImpl opcionesHorarioService;

    @BeforeEach
    void setUp() {
        disponibilidadDAO = mock(DisponibilidadDocenteDAO.class);
        docenteCursoDAO = mock(DocenteCursoDAO.class);
        cursoDAO = mock(CursoDAO.class);
        aulaDAO = mock(AulaDAO.class);
        cicloAcademicoDAO = mock(CicloAcademicoDAO.class);

        ValidadorRestriccionesHorarioService validador = this::validarSinSolapes;
        opcionesHorarioService = new OpcionesHorarioServiceImpl(
                disponibilidadDAO,
                docenteCursoDAO,
                cursoDAO,
                aulaDAO,
                cicloAcademicoDAO,
                validador);
    }

    @Test
    void generarHorariosDebeDividirCursoDeCuatroHorasEnDosSesiones() {
        Curso curso = curso(10L, "Programacion I", 4);
        when(cicloAcademicoDAO.findIdActivo()).thenReturn(Optional.of(CICLO_ID));
        when(docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(DOCENTE_ID, CICLO_ID)).thenReturn(List.of(10L));
        when(docenteCursoDAO.countDocentesByCursoIdAndCicloId(10L, CICLO_ID)).thenReturn(1);
        when(cursoDAO.findById(10L)).thenReturn(Optional.of(curso));
        when(disponibilidadDAO.findByDocenteIdAndCicloId(DOCENTE_ID, CICLO_ID))
                .thenReturn(List.of(disponibilidad("LUNES", "07:00", "11:00")));
        when(aulaDAO.findByEstadoTrue()).thenReturn(List.of(aula(1L, 1L)));

        List<OpcionesHorarioDTO> opciones = opcionesHorarioService.generarHorarios(DOCENTE_ID);

        assertEquals(3, opciones.size());
        assertEquals(2, opciones.get(0).getBloques().size());
        assertEquals("07:00", opciones.get(0).getBloques().get(0).getHoraInicio());
        assertEquals("09:00", opciones.get(0).getBloques().get(1).getHoraInicio());
    }

    @Test
    void generarHorariosNoDebeGuardarCursoParcialSiNoEntranTodasSusSesiones() {
        Curso curso = curso(10L, "Programacion I", 4);
        when(cicloAcademicoDAO.findIdActivo()).thenReturn(Optional.of(CICLO_ID));
        when(docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(DOCENTE_ID, CICLO_ID)).thenReturn(List.of(10L));
        when(docenteCursoDAO.countDocentesByCursoIdAndCicloId(10L, CICLO_ID)).thenReturn(1);
        when(cursoDAO.findById(10L)).thenReturn(Optional.of(curso));
        when(disponibilidadDAO.findByDocenteIdAndCicloId(DOCENTE_ID, CICLO_ID))
                .thenReturn(List.of(disponibilidad("LUNES", "07:00", "09:00")));
        when(aulaDAO.findByEstadoTrue()).thenReturn(List.of(aula(1L, 1L)));

        List<OpcionesHorarioDTO> opciones = opcionesHorarioService.generarHorarios(DOCENTE_ID);

        assertTrue(opciones.get(0).getBloques().isEmpty());
        assertTrue(opciones.get(0).getObservacion().contains("No se asignaron"));
    }

    private ResultadoRestriccionDTO validarSinSolapes(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales) {

        boolean tieneSolape = asignacionesActuales.stream()
                .anyMatch(asignacion -> Objects.equals(asignacion.getDiaSemana(), candidata.getDiaSemana())
                        && candidata.getHoraInicio().isBefore(asignacion.getHoraFin())
                        && asignacion.getHoraInicio().isBefore(candidata.getHoraFin()));

        return tieneSolape
                ? ResultadoRestriccionDTO.invalido("SOLAPE", "Existe cruce de horarios.")
                : ResultadoRestriccionDTO.valido();
    }

    private Curso curso(Long id, String nombre, int horas) {
        Curso curso = new Curso();
        curso.setIdCurso(id);
        curso.setNombre(nombre);
        curso.setCodigo("CUR" + id);
        curso.setTipo("TEORIA");
        curso.setHorasSemanales(horas);
        return curso;
    }

    private DisponibilidadDocente disponibilidad(String dia, String inicio, String fin) {
        DisponibilidadDocente disponibilidad = new DisponibilidadDocente();
        disponibilidad.setIdDocente(DOCENTE_ID);
        disponibilidad.setIdCicloAcademico(CICLO_ID);
        disponibilidad.setDiaSemana(dia);
        disponibilidad.setHoraInicio(LocalTime.parse(inicio));
        disponibilidad.setHoraFin(LocalTime.parse(fin));
        disponibilidad.setEstado(true);
        return disponibilidad;
    }

    private Aula aula(Long idAula, Long idSede) {
        Sede sede = new Sede();
        sede.setIdSede(idSede);
        sede.setNombre("Tacna y Arica");

        Aula aula = new Aula();
        aula.setIdAula(idAula);
        aula.setNombre("Aula " + idAula);
        aula.setTipo("TEORIA");
        aula.setSede(sede);
        aula.setEstado(true);
        return aula;
    }
}
