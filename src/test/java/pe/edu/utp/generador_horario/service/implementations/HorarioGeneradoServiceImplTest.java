package pe.edu.utp.generador_horario.service.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.HorarioGeneradoDAO;
import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;
import pe.edu.utp.generador_horario.service.interfaces.OpcionesHorarioService;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HorarioGeneradoServiceImplTest {

    @Mock
    private DocenteDAO docenteDAO;

    @Mock
    private CicloAcademicoDAO cicloAcademicoDAO;

    @Mock
    private DocenteCursoDAO docenteCursoDAO;

    @Mock
    private DisponibilidadDocenteDAO disponibilidadDocenteDAO;

    @Mock
    private HorarioGeneradoDAO horarioGeneradoDAO;

    @Mock
    private OpcionesHorarioService opcionesHorarioService;

    @InjectMocks
    private HorarioGeneradoServiceImpl horarioGeneradoService;

    @Test
    void confirmarSeleccionDocenteDebeDescartarOtrasOpcionesPendientes() {
        when(horarioGeneradoDAO.existePorDocente(33L, 7L)).thenReturn(true);

        horarioGeneradoService.confirmarSeleccionDocente(33L, 7L);

        verify(horarioGeneradoDAO).actualizarEstado(33L, "APROBADA_DOCENTE");
        verify(horarioGeneradoDAO).descartarPendientesDeDocenteExcepto(33L, 7L);
    }

    @Test
    void confirmarSeleccionDeOtroDocenteDebeLanzarErrorSinActualizar() {
        when(horarioGeneradoDAO.existePorDocente(33L, 7L)).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> horarioGeneradoService.confirmarSeleccionDocente(33L, 7L));

        verify(horarioGeneradoDAO, never()).actualizarEstado(33L, "APROBADA_DOCENTE");
        verify(horarioGeneradoDAO, never()).descartarPendientesDeDocenteExcepto(33L, 7L);
    }

    @Test
    void editarYAprobarDebeRechazarAulaOcupadaPorHorarioAprobado() {
        HorarioGeneradoResumenDTO resumen = new HorarioGeneradoResumenDTO();
        resumen.setIdHorario(33L);
        resumen.setIdDocente(7L);
        resumen.setEstado("EN_REVISION");

        DisponibilidadDocente disponibilidad = new DisponibilidadDocente();
        disponibilidad.setDiaSemana("Lunes");
        disponibilidad.setHoraInicio(LocalTime.parse("07:00"));
        disponibilidad.setHoraFin(LocalTime.parse("11:00"));

        HorarioDetalleDTO bloque = new HorarioDetalleDTO();
        bloque.setIdCurso(10L);
        bloque.setCurso("Algoritmos");
        bloque.setIdAula(5L);
        bloque.setDia("Lunes");
        bloque.setHoraInicio("07:00");
        bloque.setHoraFin("09:00");

        when(horarioGeneradoDAO.findEstadoById(33L)).thenReturn(Optional.of("EN_REVISION"));
        when(horarioGeneradoDAO.listarResumenes()).thenReturn(List.of(resumen));
        when(cicloAcademicoDAO.findIdActivo()).thenReturn(Optional.of(202601L));
        when(disponibilidadDocenteDAO.findByDocenteIdAndCicloId(7L, 202601L))
                .thenReturn(List.of(disponibilidad));
        when(horarioGeneradoDAO.existeAulaOcupadaEnHorarioAprobado(33L, 5L, "Lunes", "07:00", "09:00"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> horarioGeneradoService.editarYAprobar(33L, List.of(bloque)));

        verify(horarioGeneradoDAO, never()).reemplazarDetalles(33L, List.of(bloque));
        verify(horarioGeneradoDAO, never()).aprobar(33L);
    }
}
