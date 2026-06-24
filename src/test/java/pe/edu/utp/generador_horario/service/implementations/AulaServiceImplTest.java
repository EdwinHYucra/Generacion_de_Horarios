package pe.edu.utp.generador_horario.service.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.generador_horario.dao.AulaDAO;
import pe.edu.utp.generador_horario.entidad.Aula;
import pe.edu.utp.generador_horario.entidad.Sede;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AulaServiceImplTest {

    @Mock
    private AulaDAO aulaDAO;

    @InjectMocks
    private AulaServiceImpl aulaService;

    @Test
    void guardarAulaNuevaValidaDebePersistirConEstadoActivo() {
        Aula aula = aulaValida();

        when(aulaDAO.existsByCodigo("A-101")).thenReturn(false);
        when(aulaDAO.save(aula)).thenAnswer(invocation -> invocation.getArgument(0));

        Aula resultado = aulaService.guardarAula(aula);

        assertEquals("A-101", resultado.getCodigo());
        assertTrue(resultado.getEstado());
        verify(aulaDAO).save(aula);
    }

    @Test
    void guardarAulaSinCapacidadValidaDebeLanzarError() {
        Aula aula = aulaValida();
        aula.setCapacidad(0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aulaService.guardarAula(aula));

        assertTrue(exception.getMessage().contains("capacidad"));
        verify(aulaDAO, never()).save(any());
    }

    @Test
    void guardarAulaSinSedeDebeLanzarError() {
        Aula aula = aulaValida();
        aula.setSede(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aulaService.guardarAula(aula));

        assertTrue(exception.getMessage().contains("sede"));
        verify(aulaDAO, never()).save(any());
    }

    @Test
    void guardarAulaConCodigoDuplicadoDebeLanzarError() {
        Aula aula = aulaValida();
        when(aulaDAO.existsByCodigo("A-101")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> aulaService.guardarAula(aula));

        assertTrue(exception.getMessage().contains("aula"));
        verify(aulaDAO, never()).save(any());
    }

    private Aula aulaValida() {
        Sede sede = new Sede();
        sede.setIdSede(1L);

        Aula aula = new Aula();
        aula.setCodigo("A-101");
        aula.setNombre("Aula 101");
        aula.setTipo("TEORIA");
        aula.setCapacidad(35);
        aula.setSede(sede);
        aula.setEstado(true);
        return aula;
    }
}
