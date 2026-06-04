package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.DetalleHorario;
import com.utp.generacionhorarios.model.DisponibilidadDocente.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleHorarioRepository extends JpaRepository<DetalleHorario, Integer> {
    List<DetalleHorario> findByHorarioId(Integer horarioId);
    List<DetalleHorario> findByDocenteId(Integer docenteId);
    boolean existsByHorarioIdAndDocenteIdAndDiaSemanaAndBloqueId(
        Integer horarioId, Integer docenteId, DiaSemana diaSemana, Integer bloqueId);
    boolean existsByHorarioIdAndAulaIdAndDiaSemanaAndBloqueId(
        Integer horarioId, Integer aulaId, DiaSemana diaSemana, Integer bloqueId);
}
