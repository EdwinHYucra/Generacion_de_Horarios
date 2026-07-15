package pe.edu.utp.generador_horario.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AvisoSeguridadEmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvisoSeguridadEmailService.class);
    private final JavaMailSender mailSender;
    private final boolean habilitado;
    private final String remitente;

    public AvisoSeguridadEmailService(JavaMailSender mailSender,
            @Value("${app.mail.enabled:false}") boolean habilitado,
            @Value("${spring.mail.username:}") String remitente) {
        this.mailSender=mailSender; this.habilitado=habilitado; this.remitente=remitente;
    }

    public void avisarCambioPassword(String destinatario) {
        if (!habilitado) {
            LOGGER.info("Aviso de cambio de contraseña pendiente: SMTP no configurado. destinatario={}", destinatario);
            return;
        }
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            if (remitente != null && !remitente.isBlank()) mensaje.setFrom(remitente);
            mensaje.setTo(destinatario);
            mensaje.setSubject("Aviso de cambio de contraseña - Generador de Horarios UTP");
            mensaje.setText("Su contraseña fue cambiada correctamente el "
                    + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z"))
                    + ".\n\nSi usted no realizó este cambio, comuníquese inmediatamente con la mesa de ayuda.\n"
                    + "Teléfono: (01) 808-1234 · Opción 2\nCorreo: soporte.horarios@utp.edu.pe");
            mailSender.send(mensaje);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo enviar el aviso de cambio de contraseña a {}", destinatario, e);
        }
    }
}
