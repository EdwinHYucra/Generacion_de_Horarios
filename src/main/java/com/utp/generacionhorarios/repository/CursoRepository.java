package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
    List<Curso> findByEstadoTrue();
}
