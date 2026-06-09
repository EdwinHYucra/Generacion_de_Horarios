package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {

    List<Curso> findByEstadoTrue();

    List<Curso> findByTipoAndEstadoTrue(String tipo);
}