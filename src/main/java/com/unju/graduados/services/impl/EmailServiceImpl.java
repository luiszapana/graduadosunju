package com.unju.graduados.services.impl;

import com.unju.graduados.config.MailProperties; // ⬅️ ¡Nuevo Import!
import com.unju.graduados.services.IEmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier; // ⬅️ ¡Nuevo Import!
import org.springframework.beans.factory.annotation.Value;      // ⬅️ ¡Nuevo Import!
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {// verificacion de correos

    // 1. Inyectamos el MailSender específico para NO-REPLY
    private final JavaMailSender mailSender;

    // 2. Inyectamos el objeto de Propiedades de NO-REPLY (para obtener el username/name)
    private final MailProperties noreplyMailProps;

    // 3. Inyectamos el nombre del remitente directamente, ya que no estaba en MailProperties
    @Value("${mail.noreply.name}")
    private String senderName;

    public EmailServiceImpl(
            @Qualifier("noreplyMailSender") JavaMailSender mailSender,
            @Qualifier("noreplyMailProps") MailProperties noreplyMailProps) {
        this.mailSender = mailSender;
        this.noreplyMailProps = noreplyMailProps;
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        String url = String.format("http://localhost:8080/registro/verificar?token=%s", token);
        // --- DEBUG: Mostrar credenciales antes de enviar ---
        if (mailSender instanceof JavaMailSenderImpl impl) {
            System.out.println("DEBUG: Usando usuario -> " + impl.getUsername());
        } else {
            System.out.println("DEBUG: El mailSender no es instancia de JavaMailSenderImpl");
        }
        // ----------------------------------------------------
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = buildMessageHelper(message, to);

            // ... (HTML Content Logic) ...
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

            helper.setText(content, true);

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

    private MimeMessageHelper buildMessageHelper(MimeMessage message, String to)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // ✅ CORREGIDO: Obtener la dirección y el nombre del remitente de las Propiedades Inyectadas
        String senderEmail = noreplyMailProps.getUsername(); // ⬅️ Uso de la propiedad inyectada
        // String senderName ya está inyectado via @Value

        // Configurar los campos del correo
        helper.setFrom(senderEmail, senderName);
        helper.setTo(to);
        helper.setSubject("Verificación de cuenta");

        return helper;
    }
}