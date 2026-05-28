package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.entity.DocenteCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocenteCursoRepository extends JpaRepository<DocenteCurso, Long> {

    List<DocenteCurso> findByDocente(Docente docente);

    List<DocenteCurso> findByDocenteId(Long docenteId);

    void deleteByDocente(Docente docente);

    void deleteByDocenteId(Long docenteId);
}