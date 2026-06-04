package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Integer> {

    List<Docente> findByEstadoTrue();

}