package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.CarreraCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraCursoRepository extends JpaRepository<CarreraCurso, Long> {

    List<CarreraCurso> findByEstadoTrue();

    boolean existsByCarrera_IdCarreraAndCurso_IdCurso(Long idCarrera, Long idCurso);

    boolean existsByCarrera_IdCarreraAndCurso_IdCursoAndIdCarreraCursoNot(
            Long idCarrera,
            Long idCurso,
            Long idCarreraCurso
    );
}