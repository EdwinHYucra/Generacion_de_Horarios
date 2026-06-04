package com.utp.generacionhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.generacionhorarios.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
}
