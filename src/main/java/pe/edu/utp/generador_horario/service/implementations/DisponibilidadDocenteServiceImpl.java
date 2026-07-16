package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.BloqueDisponibilidadDTO;
import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.DisponibilidadDocenteService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio de disponibilidad docente.
 *
 * <p>Centraliza la conversion DTO-entidad para mantener el controlador liviano
 * y conservar una unica responsabilidad por capa.</p>
 */
@Service
public class DisponibilidadDocenteServiceImpl implements DisponibilidadDocenteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisponibilidadDocenteServiceImpl.class);

    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final DisponibilidadDocenteDAO disponibilidadDAO;
    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;

    public DisponibilidadDocenteServiceImpl(
            CicloAcademicoDAO cicloAcademicoDAO,
            DisponibilidadDocenteDAO disponibilidadDAO,
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO) {
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.disponibilidadDAO = disponibilidadDAO;
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
    }

    @Override
    public List<BloqueDisponibilidadDTO> listarPorEmail(String email) {
        Docente docente = obtenerDocentePorEmail(email);
        Long cicloActivoId = obtenerCicloActivoId();

        return disponibilidadDAO.findByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId)
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
        Long cicloActivoId = obtenerCicloActivoId();
        if (!disponibilidadDAO.findByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId).isEmpty()) {
            throw new IllegalStateException("La disponibilidad ya fue confirmada y no puede modificarse.");
        }

        disponibilidadDAO.deleteByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId);

        if (bloques == null) {
            LOGGER.info("Disponibilidad eliminada sin nuevos bloques. docenteId={}, cicloId={}",
                    docente.getIdDocente(), cicloActivoId);
            return;
        }

        List<BloqueDisponibilidadDTO> rangos = fusionarBloquesContiguos(bloques);

        for (BloqueDisponibilidadDTO bloque : rangos) {
            DisponibilidadDocente disponibilidad = new DisponibilidadDocente();
            disponibilidad.setIdCicloAcademico(cicloActivoId);
            disponibilidad.setIdDocente(docente.getIdDocente());
            disponibilidad.setDiaSemana(bloque.getDiaSemana());
            disponibilidad.setHoraInicio(LocalTime.parse(bloque.getHoraInicio()));
            disponibilidad.setHoraFin(LocalTime.parse(bloque.getHoraFin()));
            disponibilidad.setEstado(true);

            disponibilidadDAO.save(disponibilidad);
        }
        LOGGER.info("Disponibilidad actualizada. docenteId={}, cicloId={}, bloques={}, rangos={}",
                docente.getIdDocente(), cicloActivoId, bloques.size(), rangos.size());
    }

    private List<BloqueDisponibilidadDTO> fusionarBloquesContiguos(List<BloqueDisponibilidadDTO> bloques) {
        List<BloqueDisponibilidadDTO> ordenados = bloques.stream()
                .sorted(Comparator
                        .comparing(BloqueDisponibilidadDTO::getDiaSemana)
                        .thenComparing(BloqueDisponibilidadDTO::getHoraInicio))
                .toList();

        List<BloqueDisponibilidadDTO> rangos = new ArrayList<>();
        BloqueDisponibilidadDTO actual = null;

        for (BloqueDisponibilidadDTO bloque : ordenados) {
            if (actual == null || !actual.getDiaSemana().equals(bloque.getDiaSemana())
                    || !actual.getHoraFin().equals(bloque.getHoraInicio())) {
                actual = new BloqueDisponibilidadDTO();
                actual.setDiaSemana(bloque.getDiaSemana());
                actual.setHoraInicio(bloque.getHoraInicio());
                actual.setHoraFin(bloque.getHoraFin());
                rangos.add(actual);
                continue;
            }

            actual.setHoraFin(bloque.getHoraFin());
        }

        return rangos;
    }

    private Long obtenerCicloActivoId() {
        return cicloAcademicoDAO.findIdActivo()
                .orElseThrow(() -> new IllegalStateException("No existe un ciclo academico activo."));
    }

    private Docente obtenerDocentePorEmail(String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado para el usuario"));
    }
}
