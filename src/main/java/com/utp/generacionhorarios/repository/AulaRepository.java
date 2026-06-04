package com.utp.generacionhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.generacionhorarios.entity.Aula;

import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findByEstadoTrue();

    List<Aula> findByTipoAndEstadoTrue(Aula.TipoAula tipo);
}
