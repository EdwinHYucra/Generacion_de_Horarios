package pe.edu.utp.generador_horario.servicio.implementacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.generador_horario.dao.AdminDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la clase AdminServicioImpl.
 * Se utiliza Mockito para simular (mockear) las dependencias AdminDAO,
 * UsuarioDAO y PasswordUtil, de modo que la prueba se ejecute de forma
 * aislada sin necesidad de una conexión real a la base de datos MySQL.
 *
 * Herramienta utilizada: JUnit 5 + Mockito
 * Clase bajo prueba: AdminServicioImpl
 */
@ExtendWith(MockitoExtension.class)
class AdminServicioImplTest {

    @Mock
    private AdminDAO adminDAO;

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private AdminServicioImpl adminServicio;

    private AdminRegistroDTO dtoValido;
    private final Long SUPERADMIN_ID = 1L;

    @BeforeEach
    void setUp() {
        dtoValido = new AdminRegistroDTO();
        dtoValido.setNombre("Juan");
        dtoValido.setApellido("Pérez");
        dtoValido.setEmail("juan.perez@utp.edu.pe");
        dtoValido.setPassword("Clave1234");
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 1
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-01: Registrar administrador con datos válidos debe guardar correctamente")
    void registrarAdmin_datosValidos_debeGuardarUsuario() {
        // Arrange
        when(usuarioDAO.existeEmail(dtoValido.getEmail())).thenReturn(false);
        when(passwordUtil.encriptar(dtoValido.getPassword())).thenReturn("$2a$10$hashSimulado");

        // Act
        adminServicio.registrarAdmin(dtoValido, SUPERADMIN_ID);

        // Assert
        verify(usuarioDAO, times(1)).existeEmail(dtoValido.getEmail());
        verify(passwordUtil, times(1)).encriptar(dtoValido.getPassword());
        verify(adminDAO, times(1)).guardar(any(Usuario.class), eq(SUPERADMIN_ID));
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 2
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-02: Registrar administrador con email ya existente debe lanzar excepción")
    void registrarAdmin_emailDuplicado_debeLanzarExcepcion() {
        // Arrange
        when(usuarioDAO.existeEmail(dtoValido.getEmail())).thenReturn(true);

        // Act + Assert
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> adminServicio.registrarAdmin(dtoValido, SUPERADMIN_ID)
        );

        assertEquals("Ya existe un usuario con ese email", excepcion.getMessage());
        verify(adminDAO, never()).guardar(any(Usuario.class), anyLong());
        verify(passwordUtil, never()).encriptar(any());
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 3
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-03: La contraseña debe almacenarse encriptada, nunca en texto plano")
    void registrarAdmin_password_debeQuedarEncriptada() {
        // Arrange
        when(usuarioDAO.existeEmail(dtoValido.getEmail())).thenReturn(false);
        when(passwordUtil.encriptar("Clave1234")).thenReturn("$2a$10$hashFicticioBcrypt");

        // Act
        adminServicio.registrarAdmin(dtoValido, SUPERADMIN_ID);

        // Assert: capturamos el objeto Usuario que se envía al DAO
        org.mockito.ArgumentCaptor<Usuario> captor = org.mockito.ArgumentCaptor.forClass(Usuario.class);
        verify(adminDAO).guardar(captor.capture(), eq(SUPERADMIN_ID));

        Usuario usuarioGuardado = captor.getValue();
        assertNotEquals("Clave1234", usuarioGuardado.getPassword());
        assertEquals("$2a$10$hashFicticioBcrypt", usuarioGuardado.getPassword());
        assertEquals("ADMIN", usuarioGuardado.getRol());
        assertEquals("ACTIVO", usuarioGuardado.getEstado());
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 4
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-04: Listar administradores debe retornar la lista entregada por el DAO")
    void listarAdmins_debeRetornarListaDelDAO() {
        // Arrange
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNombre("Ana");
        u1.setEstado("ACTIVO");

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNombre("Luis");
        u2.setEstado("INACTIVO");

        List<Usuario> listaSimulada = new ArrayList<>(List.of(u1, u2));
        when(adminDAO.listarTodos()).thenReturn(listaSimulada);

        // Act
        List<Usuario> resultado = adminServicio.listarAdmins();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
        verify(adminDAO, times(1)).listarTodos();
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 5
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-05: Listar administradores cuando no existen registros debe retornar lista vacía")
    void listarAdmins_sinRegistros_debeRetornarListaVacia() {
        // Arrange
        when(adminDAO.listarTodos()).thenReturn(new ArrayList<>());

        // Act
        List<Usuario> resultado = adminServicio.listarAdmins();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 6
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-06: Buscar administrador por id existente debe retornar Optional con datos")
    void buscarPorId_idExistente_debeRetornarUsuario() {
        // Arrange
        Usuario u = new Usuario();
        u.setId(5L);
        u.setNombre("Carlos");
        when(adminDAO.buscarPorId(5L)).thenReturn(Optional.of(u));

        // Act
        Optional<Usuario> resultado = adminServicio.buscarPorId(5L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Carlos", resultado.get().getNombre());
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 7
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-07: Buscar administrador por id inexistente debe retornar Optional vacío")
    void buscarPorId_idInexistente_debeRetornarOptionalVacio() {
        // Arrange
        when(adminDAO.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = adminServicio.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 8
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-08: Desactivar administrador debe invocar cambio de estado a INACTIVO")
    void desactivarAdmin_debeCambiarEstadoAInactivo() {
        // Act
        adminServicio.desactivarAdmin(3L);

        // Assert
        verify(adminDAO, times(1)).cambiarEstado(3L, "INACTIVO");
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 9
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-09: Activar administrador debe invocar cambio de estado a ACTIVO")
    void activarAdmin_debeCambiarEstadoAActivo() {
        // Act
        adminServicio.activarAdmin(3L);

        // Assert
        verify(adminDAO, times(1)).cambiarEstado(3L, "ACTIVO");
    }

    // ────────────────────────────────────────────────────────
    // PRUEBA 10
    // ────────────────────────────────────────────────────────
    @Test
    @DisplayName("PU-10: Actualizar administrador debe delegar correctamente al DAO")
    void actualizarAdmin_debeInvocarActualizarEnDAO() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Pedro");
        usuario.setApellido("Gómez");
        usuario.setEmail("pedro.gomez@utp.edu.pe");

        // Act
        adminServicio.actualizarAdmin(usuario);

        // Assert
        verify(adminDAO, times(1)).actualizar(usuario);
    }
}