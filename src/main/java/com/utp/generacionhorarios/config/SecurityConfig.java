package com.utp.generacionhorarios.config;

import com.utp.generacionhorarios.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad del sistema.
 * Gestiona la autenticación, autorización
 * y acceso a recursos protegidos.
 *
 * Define los roles ADMIN y DOCENTE,
 * así como el proceso de inicio y cierre de sesión.
 *
 * @author Dayanna
 */

@Configuration
public class SecurityConfig {

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService) {
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/login",
                        "/css/**",
                        "/img/**",
                        "/js/**"
                ).permitAll()

                .requestMatchers("/admin/**")
                .hasRole("ADMIN")

                .requestMatchers("/docente/**")
                .hasRole("DOCENTE")

                .anyRequest()
                .authenticated()
            )

            .formLogin(login -> login

                .loginPage("/login")

                .usernameParameter("username")

                .passwordParameter("password")

                .defaultSuccessUrl("/redireccionar", true)

                .permitAll()
            )

            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/login?logout")

                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }
}