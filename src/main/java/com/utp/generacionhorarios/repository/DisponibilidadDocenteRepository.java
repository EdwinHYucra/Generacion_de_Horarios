package com.utp.generacionhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utp.generacionhorarios.entity.DisponibilidadDocente;
import com.utp.generacionhorarios.entity.DisponibilidadDocente.DiaSemana;

import java.util.List;

@Repository
public interface DisponibilidadDocenteRepository
        extends JpaRepository<DisponibilidadDocente, Integer> {

    List<DisponibilidadDocente> findByDocenteIdAndSemestreId(
            Integer docenteId,
            Integer semestreId);

    List<DisponibilidadDocente> findByDocenteIdAndSemestreIdAndDiaSemana(
            Integer docenteId,
            Integer semestreId,
            DiaSemana diaSemana);
}