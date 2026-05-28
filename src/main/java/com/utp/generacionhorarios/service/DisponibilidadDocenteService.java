package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.dto.BloqueDisponibilidadDTO;
import com.utp.generacionhorarios.dto.DisponibilidadDocenteDTO;
import com.utp.generacionhorarios.entity.DisponibilidadDocente;
import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.repository.DisponibilidadDocenteRepository;
import com.utp.generacionhorarios.repository.DocenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadDocenteService {

    private final DisponibilidadDocenteRepository disponibilidadDocenteRepository;
    private final DocenteRepository docenteRepository;

    public DisponibilidadDocenteService(
            DisponibilidadDocenteRepository disponibilidadDocenteRepository,
            DocenteRepository docenteRepository) {
        this.disponibilidadDocenteRepository = disponibilidadDocenteRepository;
        this.docenteRepository = docenteRepository;
    }

    public List<String> obtenerDiasSemana() {
        return List.of(
                "LUNES",
                "MARTES",
                "MIERCOLES",
                "JUEVES",
                "VIERNES",
                "SABADO",
                "DOMINGO");
    }

    public List<String> obtenerBloquesHorario() {
        List<String> bloques = new ArrayList<>();

        LocalTime horaInicio = LocalTime.of(7, 0);
        LocalTime horaFin = LocalTime.of(22, 30);

        while (horaInicio.isBefore(horaFin)) {
            bloques.add(horaInicio.toString());
            horaInicio = horaInicio.plusMinutes(15);
        }

        return bloques;
    }

    public List<DisponibilidadDocente> obtenerDisponibilidadPorDocente(Long docenteId) {
        return disponibilidadDocenteRepository.findByDocenteId(docenteId);
    }

    @Transactional
    public void guardarDisponibilidad(DisponibilidadDocenteDTO disponibilidadDocenteDTO) {

        if (disponibilidadDocenteDTO.getDocenteId() == null) {
            throw new IllegalArgumentException("No se encontró el docente.");
        }

        if (disponibilidadDocenteDTO.getBloques() == null ||
                disponibilidadDocenteDTO.getBloques().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un bloque de disponibilidad.");
        }

        Docente docente = docenteRepository.findById(disponibilidadDocenteDTO.getDocenteId())
                .orElseThrow(() -> new IllegalArgumentException("El docente no existe."));

        disponibilidadDocenteRepository.deleteByDocenteId(docente.getId());

        List<DisponibilidadDocente> disponibilidades = new ArrayList<>();

        for (BloqueDisponibilidadDTO bloqueDTO : disponibilidadDocenteDTO.getBloques()) {

            if (bloqueDTO.getDiaSemana() == null ||
                    bloqueDTO.getHoraInicio() == null ||
                    bloqueDTO.getHoraFin() == null) {
                continue;
            }

            DisponibilidadDocente disponibilidad = new DisponibilidadDocente();
            disponibilidad.setDocente(docente);
            disponibilidad.setDiaSemana(bloqueDTO.getDiaSemana());
            disponibilidad.setHoraInicio(LocalTime.parse(bloqueDTO.getHoraInicio()));
            disponibilidad.setHoraFin(LocalTime.parse(bloqueDTO.getHoraFin()));
            disponibilidad.setEstado(1);
            disponibilidad.setFechaCreacion(LocalDateTime.now());

            disponibilidades.add(disponibilidad);
        }

        if (disponibilidades.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un bloque válido.");
        }

        disponibilidadDocenteRepository.saveAll(disponibilidades);
    }
}