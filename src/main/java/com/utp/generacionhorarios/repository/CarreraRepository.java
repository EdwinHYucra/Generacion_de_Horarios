package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    List<Carrera> findByEstadoTrue();

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    boolean existsByCodigoAndIdCarreraNot(String codigo, Long idCarrera);

    boolean existsByNombreAndIdCarreraNot(String nombre, Long idCarrera);
}