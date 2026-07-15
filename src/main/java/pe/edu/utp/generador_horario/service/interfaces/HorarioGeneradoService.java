package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;

import java.util.List;
import java.util.Optional;

/**
 * Orquesta la generacion, revision y aprobacion de horarios.
 */
public interface HorarioGeneradoService {

    int generarParaDocente(Long idDocente);

    int generarSiTieneInsumos(Long idDocente);

    int generarParaTodos();

    List<HorarioGeneradoResumenDTO> listarResumenes();

    List<OpcionesHorarioDTO> listarOpcionesPendientesPorDocente(Long idDocente);

    Optional<HorarioGeneradoResumenDTO> buscarAprobadoPorDocente(Long idDocente);

    List<HorarioDetalleDTO> listarDetalles(Long idHorario);

    void aprobar(Long idHorario);

    void rechazarPorAdministrador(Long idHorario);

    void editarYAprobar(Long idHorario, List<HorarioDetalleDTO> detalles);

    void confirmarSeleccionDocente(Long idHorario, Long idDocente);

    void marcarEnRevision(Long idHorario, Long idDocente);

    void rechazarPorDocente(Long idHorario, Long idDocente);
}
