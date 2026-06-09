package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByEstadoTrue();

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    boolean existsByCodigoAndIdCursoNot(String codigo, Long idCurso);

    boolean existsByNombreAndIdCursoNot(String nombre, Long idCurso);
}