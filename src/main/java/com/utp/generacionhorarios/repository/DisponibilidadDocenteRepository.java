package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.DisponibilidadDocente;
import com.utp.generacionhorarios.model.DisponibilidadDocente.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibilidadDocenteRepository extends JpaRepository<DisponibilidadDocente, Integer> {
    List<DisponibilidadDocente> findByDocenteIdAndSemestreId(Integer docenteId, Integer semestreId);
    List<DisponibilidadDocente> findByDocenteIdAndSemestreIdAndDiaSemana(
        Integer docenteId, Integer semestreId, DiaSemana diaSemana);
}
