package pe.edu.utp.generador_horario.service.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.generador_horario.dao.CarreraCursoDAO;
import pe.edu.utp.generador_horario.entidad.Carrera;
import pe.edu.utp.generador_horario.entidad.CarreraCurso;
import pe.edu.utp.generador_horario.entidad.Curso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarreraCursoServiceImplTest {

    @Mock
    private CarreraCursoDAO carreraCursoDAO;

    @InjectMocks
    private CarreraCursoServiceImpl carreraCursoService;

    @Test
    void guardarAsignacionNuevaValidaDebePersistirConEstadoActivo() {
        CarreraCurso asignacion = asignacionValida();

        when(carreraCursoDAO.existsByCarrera_IdCarreraAndCurso_IdCurso(1L, 10L)).thenReturn(false);
        when(carreraCursoDAO.save(asignacion)).thenAnswer(invocation -> invocation.getArgument(0));

        CarreraCurso resultado = carreraCursoService.guardarAsignacion(asignacion);

        assertTrue(resultado.getEstado());
        assertEquals(3, resultado.getCiclo());
        verify(carreraCursoDAO).save(asignacion);
    }

    @Test
    void guardarAsignacionSinCarreraDebeLanzarError() {
        CarreraCurso asignacion = asignacionValida();
        asignacion.setCarrera(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carreraCursoService.guardarAsignacion(asignacion));

        assertTrue(exception.getMessage().contains("carrera"));
        verify(carreraCursoDAO, never()).save(any());
    }

    @Test
    void guardarAsignacionConCicloFueraDeRangoDebeLanzarError() {
        CarreraCurso asignacion = asignacionValida();
        asignacion.setCiclo(11);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carreraCursoService.guardarAsignacion(asignacion));

        assertTrue(exception.getMessage().contains("ciclo"));
        verify(carreraCursoDAO, never()).save(any());
    }

    @Test
    void guardarAsignacionDuplicadaDebeLanzarError() {
        CarreraCurso asignacion = asignacionValida();
        when(carreraCursoDAO.existsByCarrera_IdCarreraAndCurso_IdCurso(1L, 10L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carreraCursoService.guardarAsignacion(asignacion));

        assertTrue(exception.getMessage().contains("asignado"));
        verify(carreraCursoDAO, never()).save(any());
    }

    private CarreraCurso asignacionValida() {
        Carrera carrera = new Carrera();
        carrera.setIdCarrera(1L);

        Curso curso = new Curso();
        curso.setIdCurso(10L);

        CarreraCurso asignacion = new CarreraCurso();
        asignacion.setCarrera(carrera);
        asignacion.setCurso(curso);
        asignacion.setCiclo(3);
        asignacion.setEstado(true);
        return asignacion;
    }
}
