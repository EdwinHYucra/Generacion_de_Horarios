package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;

import java.util.List;
import java.util.Optional;

/**
 * DAO JDBC para persistir y consultar horarios generados.
 */
public interface HorarioGeneradoDAO {

    Long saveHorario(Long idDocente, OpcionesHorarioDTO opcion, String estado);

    void saveDetalle(Long idHorario, HorarioDetalleDTO detalle);

    void eliminarPendientesPorDocente(Long idDocente);

    List<HorarioGeneradoResumenDTO> listarResumenes();

    List<HorarioGeneradoResumenDTO> listarPendientesPorDocente(Long idDocente);

    boolean existePorDocente(Long idHorario, Long idDocente);

    Optional<HorarioGeneradoResumenDTO> buscarAprobadoPorDocente(Long idDocente);

    List<HorarioDetalleDTO> listarDetalles(Long idHorario);

    void aprobar(Long idHorario);
}
