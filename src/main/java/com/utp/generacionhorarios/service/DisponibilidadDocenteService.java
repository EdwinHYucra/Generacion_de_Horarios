package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.dto.DisponibilidadDocenteDTO;
import com.utp.generacionhorarios.model.DisponibilidadDocente;
import com.utp.generacionhorarios.repository.DisponibilidadDocenteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadDocenteService {

    private final DisponibilidadDocenteRepository disponibilidadDocenteRepository;

    public DisponibilidadDocenteService(
            DisponibilidadDocenteRepository disponibilidadDocenteRepository) {
        this.disponibilidadDocenteRepository = disponibilidadDocenteRepository;
    }

    public List<String> obtenerDiasSemana() {
        return List.of(
                "LUNES",
                "MARTES",
                "MIERCOLES",
                "JUEVES",
                "VIERNES",
                "SABADO");
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
        return new ArrayList<>();
    }

    public void guardarDisponibilidad(DisponibilidadDocenteDTO disponibilidadDocenteDTO) {

        if (disponibilidadDocenteDTO.getDocenteId() == null) {
            throw new IllegalArgumentException("No se encontró el docente.");
        }

        if (disponibilidadDocenteDTO.getBloques() == null ||
                disponibilidadDocenteDTO.getBloques().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un bloque de disponibilidad.");
        }

        // Temporal:
        // Luego se conectará con el modelo nuevo de Dayanna/Álvaro:
        // Docente + Semestre + BloqueHorario.
    }
}