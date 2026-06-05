package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    List<Aula> findByEstadoTrue();

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdAulaNot(String codigo, Long idAula);
}