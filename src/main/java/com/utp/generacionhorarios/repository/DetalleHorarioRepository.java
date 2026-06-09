package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.DetalleHorario;
import com.utp.generacionhorarios.entity.DisponibilidadDocente.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DetalleHorarioRepository extends JpaRepository<DetalleHorario, Long> {

    @Query("SELECT d FROM DetalleHorario d WHERE d.horario.id = :horarioId")
    List<DetalleHorario> findByHorarioId(@Param("horarioId") Integer horarioId);

    @Query("SELECT d FROM DetalleHorario d WHERE d.docente.idDocente = :docenteId")
    List<DetalleHorario> findByDocenteId(@Param("docenteId") Long docenteId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DetalleHorario d " +
           "WHERE d.horario.id = :horarioId AND d.docente.idDocente = :docenteId " +
           "AND d.diaSemana = :diaSemana AND d.bloque.id = :bloqueId")
    boolean existsByHorarioIdAndDocenteIdAndDiaSemanaAndBloqueId(
            @Param("horarioId") Integer horarioId,
            @Param("docenteId") Long docenteId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("bloqueId") Integer bloqueId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DetalleHorario d " +
           "WHERE d.horario.id = :horarioId AND d.aula.idAula = :aulaId " +
           "AND d.diaSemana = :diaSemana AND d.bloque.id = :bloqueId")
    boolean existsByHorarioIdAndAulaIdAndDiaSemanaAndBloqueId(
            @Param("horarioId") Integer horarioId,
            @Param("aulaId") Long aulaId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("bloqueId") Integer bloqueId);
}