package com.utp.generacionhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.generacionhorarios.entity.SolicitudCambio;

import java.util.List;

public interface SolicitudCambioRepository extends JpaRepository<SolicitudCambio, Integer> {
    List<SolicitudCambio> findByDocenteId(Integer docenteId);

    List<SolicitudCambio> findByEstado(SolicitudCambio.EstadoCambio estado);
}
