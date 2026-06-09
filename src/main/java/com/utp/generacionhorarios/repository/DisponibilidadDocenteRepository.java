package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.DisponibilidadDocente;
import com.utp.generacionhorarios.entity.DisponibilidadDocente.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisponibilidadDocenteRepository
        extends JpaRepository<DisponibilidadDocente, Long> {

    @Query("SELECT d FROM DisponibilidadDocente d WHERE d.docente.idDocente = :docenteId AND d.semestre.id = :semestreId")
    List<DisponibilidadDocente> findByDocenteIdAndSemestreId(
            @Param("docenteId") Long docenteId,
            @Param("semestreId") Integer semestreId);

    @Query("SELECT d FROM DisponibilidadDocente d WHERE d.docente.idDocente = :docenteId AND d.semestre.id = :semestreId AND d.diaSemana = :diaSemana")
    List<DisponibilidadDocente> findByDocenteIdAndSemestreIdAndDiaSemana(
            @Param("docenteId") Long docenteId,
            @Param("semestreId") Integer semestreId,
            @Param("diaSemana") DiaSemana diaSemana);
}