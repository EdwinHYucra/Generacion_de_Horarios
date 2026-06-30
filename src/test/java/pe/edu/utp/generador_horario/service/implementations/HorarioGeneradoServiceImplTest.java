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
import pe.edu.utp.generador_horario.service.interfaces.OpcionesHorarioService;

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
}
