package pe.edu.utp.generador_horario.service.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.generador_horario.dao.RestriccionSedeDAO;
import pe.edu.utp.generador_horario.entidad.RestriccionSede;
import pe.edu.utp.generador_horario.entidad.Sede;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestriccionSedeServiceImplTest {

    @Mock
    private RestriccionSedeDAO restriccionSedeDAO;

    @InjectMocks
    private RestriccionSedeServiceImpl restriccionSedeService;

    @Test
    void guardarRestriccionValidaDebePersistir() {
        RestriccionSede restriccion = restriccionValida();

        when(restriccionSedeDAO.existsBySedes(1L, 2L)).thenReturn(false);
        when(restriccionSedeDAO.save(restriccion)).thenReturn(restriccion);

        RestriccionSede resultado = restriccionSedeService.guardar(restriccion);

        assertEquals(30, resultado.getTiempoMinimoMinutos());
        verify(restriccionSedeDAO).save(restriccion);
    }

    @Test
    void guardarRestriccionConMismaSedeDebeLanzarError() {
        RestriccionSede restriccion = restriccionValida();
        restriccion.setSedeDestino(sede(1L));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> restriccionSedeService.guardar(restriccion));

        assertTrue(exception.getMessage().contains("diferentes"));
        verify(restriccionSedeDAO, never()).save(any());
    }

    @Test
    void guardarRestriccionConTiempoNegativoDebeLanzarError() {
        RestriccionSede restriccion = restriccionValida();
        restriccion.setTiempoMinimoMinutos(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> restriccionSedeService.guardar(restriccion));

        assertTrue(exception.getMessage().contains("tiempo"));
        verify(restriccionSedeDAO, never()).save(any());
    }

    @Test
    void guardarRestriccionDuplicadaDebeLanzarError() {
        RestriccionSede restriccion = restriccionValida();
        when(restriccionSedeDAO.existsBySedes(1L, 2L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> restriccionSedeService.guardar(restriccion));

        assertTrue(exception.getMessage().contains("Ya existe"));
        verify(restriccionSedeDAO, never()).save(any());
    }

    private RestriccionSede restriccionValida() {
        RestriccionSede restriccion = new RestriccionSede();
        restriccion.setSedeOrigen(sede(1L));
        restriccion.setSedeDestino(sede(2L));
        restriccion.setTiempoMinimoMinutos(30);
        return restriccion;
    }

    private Sede sede(Long id) {
        Sede sede = new Sede();
        sede.setIdSede(id);
        return sede;
    }
}
