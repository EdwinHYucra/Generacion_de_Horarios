package pe.edu.utp.generador_horario.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encriptar(String password) {
        return encoder.encode(password);
    }

    public boolean verificar(String passwordPlano, String passwordEncriptado) {
        return encoder.matches(passwordPlano, passwordEncriptado);
    }
}