package pe.edu.utp.generador_horario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Usuario;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    private final UsuarioDAO usuarioDAO;

    public SeguridadConfig(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Optional<Usuario> opcional = usuarioDAO.buscarPorEmail(email);
            Usuario usuario = opcional
                    .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                            "Usuario no encontrado: " + email));
            return org.springframework.security.core.userdetails.User
                    .withUsername(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles(usuario.getRol())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/assets/**").permitAll()
                        .requestMatchers("/evaluacion-docente/**").permitAll()
                        .requestMatchers(RutasSistema.LOGIN, RutasSistema.LOGOUT).permitAll()
                        .requestMatchers(RutasSistema.SUPERADMIN + "/**").hasRole(RolSistema.SUPERADMIN)
                        .requestMatchers(RutasSistema.ADMINISTRADOR + "/**").hasRole(RolSistema.ADMIN)
                        .requestMatchers(RutasSistema.DOCENTE + "/**").hasRole(RolSistema.DOCENTE)
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage(RutasSistema.LOGIN)
                        .successHandler((request, response, authentication) -> {
                            boolean esSuperAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> authority.getAuthority().equals(RolSistema.AUTHORITY_SUPERADMIN));

                            boolean esAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> authority.getAuthority().equals(RolSistema.AUTHORITY_ADMIN));

                            boolean esDocente = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> authority.getAuthority().equals(RolSistema.AUTHORITY_DOCENTE));

                            if (esSuperAdmin) {
                                response.sendRedirect(RutasSistema.SUPERADMIN_DASHBOARD);
                            } else if (esAdmin) {
                                response.sendRedirect(RutasSistema.ADMINISTRADOR_DASHBOARD);
                            } else if (esDocente) {
                                response.sendRedirect(RutasSistema.DOCENTE_DASHBOARD);
                            } else {
                                response.sendRedirect(RutasSistema.LOGIN_ERROR);
                            }
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl(RutasSistema.LOGOUT)
                        .logoutSuccessUrl(RutasSistema.LOGIN + "?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}
