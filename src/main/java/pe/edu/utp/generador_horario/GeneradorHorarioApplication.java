package pe.edu.utp.generador_horario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class GeneradorHorarioApplication {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Admin1234: " + encoder.encode("Admin1234"));
        System.out.println("Percy1234: " + encoder.encode("Percy1234"));
        System.out.println("Docente1234: " + encoder.encode("Docente1234"));

        SpringApplication.run(GeneradorHorarioApplication.class, args);
    }
}