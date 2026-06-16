package pe.edu.utp.generador_horario.service.implementations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.BloqueDisponibilidadDTO;
import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.DisponibilidadDocenteService;

import java.time.LocalTime;
import java.util.List;

@Service
public class DisponibilidadDocenteServiceImpl implements DisponibilidadDocenteService {

    private final DisponibilidadDocenteDAO disponibilidadDAO;
    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;

    public DisponibilidadDocenteServiceImpl(
            DisponibilidadDocenteDAO disponibilidadDAO,
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO) {
        this.disponibilidadDAO = disponibilidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
    }

    @Override
    public List<BloqueDisponibilidadDTO> listarPorEmail(String email) {
        Docente docente = obtenerDocentePorEmail(email);

        return disponibilidadDAO.findByDocenteId(docente.getIdDocente())
                .stream()
                .map(d -> {
                    BloqueDisponibilidadDTO dto = new BloqueDisponibilidadDTO();
                    dto.setDiaSemana(d.getDiaSemana());
                    dto.setHoraInicio(d.getHoraInicio().toString());
                    dto.setHoraFin(d.getHoraFin().toString());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void guardarPorEmail(String email, List<BloqueDisponibilidadDTO> bloques) {
        Docente docente = obtenerDocentePorEmail(email);

        disponibilidadDAO.deleteByDocenteId(docente.getIdDocente());

        if (bloques == null)
            return;

        for (BloqueDisponibilidadDTO bloque : bloques) {
            DisponibilidadDocente disponibilidad = new DisponibilidadDocente();
            disponibilidad.setIdDocente(docente.getIdDocente());
            disponibilidad.setDiaSemana(bloque.getDiaSemana());
            disponibilidad.setHoraInicio(LocalTime.parse(bloque.getHoraInicio()));
            disponibilidad.setHoraFin(LocalTime.parse(bloque.getHoraFin()));
            disponibilidad.setEstado(true);

            disponibilidadDAO.save(disponibilidad);
        }
    }

    private Docente obtenerDocentePorEmail(String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado para el usuario"));
    }
}