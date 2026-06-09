package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.EvaluacionDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluacionDocenteRepository extends JpaRepository<EvaluacionDocente, Long> {

    @Query("SELECT e FROM EvaluacionDocente e WHERE e.docente.idDocente = :docenteId AND e.curso.idCurso = :cursoId")
    List<EvaluacionDocente> findByDocenteAndCurso(
            @Param("docenteId") Long docenteId,
            @Param("cursoId") Long cursoId);

    @Query("SELECT e FROM EvaluacionDocente e WHERE e.docente.idDocente = :docenteId")
    List<EvaluacionDocente> findByDocente(@Param("docenteId") Long docenteId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EvaluacionDocente e " +
           "WHERE e.docente.idDocente = :docenteId AND e.curso.idCurso = :cursoId " +
           "AND e.calificacionPromedio < :notaMinima")
    boolean existeEvaluacionBaja(
            @Param("docenteId") Long docenteId,
            @Param("cursoId") Long cursoId,
            @Param("notaMinima") Double notaMinima);
}