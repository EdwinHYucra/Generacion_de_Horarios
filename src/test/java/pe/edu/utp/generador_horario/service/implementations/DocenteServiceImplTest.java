package pe.edu.utp.generador_horario.service.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.factory.UsuarioFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocenteServiceImplTest {

    @Mock
    private DocenteDAO docenteDAO;

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private UsuarioFactory usuarioFactory;

    @InjectMocks
    private DocenteServiceImpl docenteService;

    @Test
    void guardarDocenteNuevoDebeCrearUsuarioYAsignarloAlDocente() {
        Docente docente = docenteNuevo();
        Usuario usuario = new Usuario();
        usuario.setEmail(docente.getCorreo());

        when(usuarioDAO.existeEmail(docente.getCorreo())).thenReturn(false);
        when(usuarioFactory.crearDocente(docente)).thenReturn(usuario);
        when(usuarioDAO.guardarRetornandoId(usuario)).thenReturn(15L);
        when(docenteDAO.save(docente)).thenAnswer(invocation -> invocation.getArgument(0));

        Docente resultado = docenteService.guardarDocente(docente);

        assertEquals(15L, resultado.getUsuarioId());
        assertTrue(resultado.getEstado());
        verify(usuarioFactory).crearDocente(docente);
        verify(usuarioDAO).guardarRetornandoId(usuario);
        verify(docenteDAO).save(docente);
    }

    @Test
    void guardarDocenteNuevoSinPasswordDebeRechazarRegistro() {
        Docente docente = docenteNuevo();
        docente.setPassword(" ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> docenteService.guardarDocente(docente));

        assertTrue(exception.getMessage().contains("obligatoria"));
        verify(usuarioDAO, never()).guardarRetornandoId(any());
        verify(docenteDAO, never()).save(any());
    }

    @Test
    void guardarDocenteNuevoConCorreoExistenteDebeRechazarRegistro() {
        Docente docente = docenteNuevo();
        when(usuarioDAO.existeEmail(docente.getCorreo())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> docenteService.guardarDocente(docente));

        assertTrue(exception.getMessage().contains("correo"));
        verify(usuarioFactory, never()).crearDocente(any());
        verify(docenteDAO, never()).save(any());
    }

    private Docente docenteNuevo() {
        Docente docente = new Docente();
        docente.setCodigo("DOC100");
        docente.setNombres("Ana");
        docente.setApellidos("Torres");
        docente.setCorreo("ana.torres@utp.edu.pe");
        docente.setPassword("Admin1234");
        docente.setDni("12345678");
        docente.setEstado(true);
        return docente;
    }
}
