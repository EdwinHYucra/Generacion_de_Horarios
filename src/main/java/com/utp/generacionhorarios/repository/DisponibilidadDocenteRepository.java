package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.DisponibilidadDocente;
import com.utp.generacionhorarios.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibilidadDocenteRepository extends JpaRepository<DisponibilidadDocente, Long> {

    List<DisponibilidadDocente> findByDocente(Docente docente);

    List<DisponibilidadDocente> findByDocenteId(Long docenteId);

    void deleteByDocente(Docente docente);

    void deleteByDocenteId(Long docenteId);
}