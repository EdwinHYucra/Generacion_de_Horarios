package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.entity.DocenteCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface DocenteCursoRepository extends JpaRepository<DocenteCurso, Long> {
    List<DocenteCurso> findByDocente(Docente docente);
    List<DocenteCurso> findByDocente_IdDocente(Long idDocente);

    @Modifying
    @Transactional
    @Query("DELETE FROM DocenteCurso dc WHERE dc.docente.idDocente = :idDocente")
    void deleteByDocenteIdDocente(@Param("idDocente") Long idDocente);
}