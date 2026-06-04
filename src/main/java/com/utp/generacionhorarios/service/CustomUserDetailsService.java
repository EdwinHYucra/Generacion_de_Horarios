package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.model.Usuario;
import com.utp.generacionhorarios.repository.UsuarioRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de cargar los datos
 * de autenticación de los usuarios desde
 * la base de datos.
 *
 * Implementa UserDetailsService para
 * integrarse con Spring Security.
 *
 * @author Dayanna
 */ 

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    /**
 * Busca un usuario por su nombre de usuario
 * y construye los permisos necesarios para
 * Spring Security.
 *
 * @param username nombre de usuario
 * @return información de autenticación
 * @throws UsernameNotFoundException si el usuario no existe
 */

@Override
public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"));

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + usuario.getRol().name()
                        )
                )
        );
    }
}

