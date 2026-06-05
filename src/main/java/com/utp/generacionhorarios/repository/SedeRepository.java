package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

    List<Sede> findByEstadoTrue();

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    boolean existsByCodigoAndIdSedeNot(String codigo, Long idSede);

    boolean existsByNombreAndIdSedeNot(String nombre, Long idSede);
}