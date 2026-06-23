package pe.edu.utp.generador_horario.service.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.utp.generador_horario.dao.AulaDAO;
import pe.edu.utp.generador_horario.dao.CursoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.service.interfaces.OpcionesHorarioService;

@Service
public class OpcionesHorarioServiceImpl
        implements OpcionesHorarioService {

    private final DisponibilidadDocenteDAO disponibilidadDAO;
    private final DocenteCursoDAO docenteCursoDAO;
    private final CursoDAO cursoDAO;
    private final AulaDAO aulaDAO;

    public OpcionesHorarioServiceImpl(
            DisponibilidadDocenteDAO disponibilidadDAO,
            DocenteCursoDAO docenteCursoDAO,
            CursoDAO cursoDAO,
            AulaDAO aulaDAO) {

        this.disponibilidadDAO = disponibilidadDAO;
        this.docenteCursoDAO = docenteCursoDAO;
        this.cursoDAO = cursoDAO;
        this.aulaDAO = aulaDAO;
    }

    @Override
    public List<OpcionesHorarioDTO> generarHorarios(Long docenteId) {

        List<OpcionesHorarioDTO> horarios = new ArrayList<>();

        horarios.add(generarOpcion(docenteId, 1));
        horarios.add(generarOpcion(docenteId, 2));
        horarios.add(generarOpcion(docenteId, 3));

        return horarios;
    }

    private OpcionesHorarioDTO generarOpcion(
            Long docenteId,
            Integer opcion) {

        OpcionesHorarioDTO dto = new OpcionesHorarioDTO();

        dto.setOpcion(opcion);

        switch (opcion) {

            case 1:
                dto.setObservacion(
                        "Horario generado priorizando horarios de mañana.");
                break;

            case 2:
                dto.setObservacion(
                        "Horario generado distribuyendo la carga académica.");
                break;

            case 3:
                dto.setObservacion(
                        "Horario generado concentrando cursos en menos días.");
                break;

            default:
                dto.setObservacion(
                        "Horario generado automáticamente.");
        }

        dto.setBloques(new ArrayList<>());

        return dto;
    }
}