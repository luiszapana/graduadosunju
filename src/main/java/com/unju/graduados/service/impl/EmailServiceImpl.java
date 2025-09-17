package com.unju.graduados.service.impl;

import com.unju.graduados.service.IEmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String url = String.format("http://localhost:8080/registro/verificar?token=%s", token);
        // --- DEBUG: Mostrar credenciales antes de enviar ---
        if (mailSender instanceof JavaMailSenderImpl impl) {
            System.out.println("DEBUG: Usando usuario -> " + impl.getUsername());
            System.out.println("DEBUG: Usando contraseña -> " + impl.getPassword());
        } else {
            System.out.println("DEBUG: El mailSender no es instancia de JavaMailSenderImpl");
        }
        // ----------------------------------------------------
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Verificación de cuenta");

            String content = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>¡Bienvenido a Graduados UNJU!</h2>
                    <p>Gracias por registrarte. Por favor haz clic en el siguiente botón para verificar tu cuenta:</p>
                    <p>
                        <a href="%s" style="display:inline-block; padding:10px 20px; 
                            background-color:#007BFF; color:#ffffff; text-decoration:none; border-radius:5px;">
                            Verificar cuenta
                        </a>
                    </p>
                    <p>O copia y pega este enlace en tu navegador:</p>
                    <p><a href="%s">%s</a></p>
                    <br>
                    <p style="font-size:12px; color:#666;">Este correo es automático, por favor no respondas.</p>
                </body>
                </html>
                """.formatted(url, url, url);

            helper.setText(content, true); // true = permite HTML

            mailSender.send(message);
            log.info("[EMAIL] Enviado email de verificación a {}", to);

        } catch (Exception e) {
            log.error("[EMAIL] Error enviando email a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el email de verificación", e);
        }
    }

    @PostConstruct
    public void testMailProps() {
        if (mailSender instanceof JavaMailSenderImpl impl) {
            log.info("Username: {}", impl.getUsername());
            log.info("Password is set? {}", impl.getPassword() != null && !impl.getPassword().isBlank());
        } else {
            log.warn("El mailSender no es instancia de JavaMailSenderImpl");
        }
    }
}
