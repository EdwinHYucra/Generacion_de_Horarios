package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.DisponibilidadDocente;
import com.utp.generacionhorarios.model.DisponibilidadDocente.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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