package pe.edu.utp.generador_horario.service.implementations;

import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.DocenteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementa las operaciones de negocio para docentes.
 *
 * <p>Administra el registro, consulta y desactivacion logica de docentes.</p>
 *
 * @author Edwin
 */
@Service
public class DocenteServiceImpl implements DocenteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocenteServiceImpl.class);

    private final DocenteDAO docenteDAO;
    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    public DocenteServiceImpl(DocenteDAO docenteDAO, UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.docenteDAO = docenteDAO;
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Docente> listarDocentes() {
        return docenteDAO.findAll();
    }

    @Override
    public Docente obtenerPorId(Long id) {
        return docenteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Docente guardarDocente(Docente docente) {
        if (docente.getEstado() == null) {
            docente.setEstado(true);
        }

        boolean esNuevo = docente.getIdDocente() == null;

        if (esNuevo) {
            if (docente.getPassword() == null || docente.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para registrar un docente.");
            }

            if (usuarioDAO.existeEmail(docente.getCorreo())) {
                throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo.");
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(docente.getNombres());
            usuario.setApellido(docente.getApellidos());
            usuario.setEmail(docente.getCorreo());
            usuario.setPassword(passwordEncoder.encode(docente.getPassword()));
            usuario.setRol("DOCENTE");
            usuario.setEstado(Boolean.TRUE.equals(docente.getEstado()) ? "ACTIVO" : "INACTIVO");

            Long usuarioId = usuarioDAO.guardarRetornandoId(usuario);
            docente.setUsuarioId(usuarioId);
        }

        try {
            return docenteDAO.save(docente);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar el docente. id={}, codigo={}, dni={}",
                    docente.getIdDocente(), docente.getCodigo(), docente.getDni(), e);
            throw e;
        }
    }

    @Override
    public void desactivarDocente(Long id) {
        Docente docente = obtenerPorId(id);
        docente.setEstado(false);
        try {
            docenteDAO.save(docente);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la desactivacion del docente. id={}", id, e);
            throw e;
        }
    }
}

