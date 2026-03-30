package com.unju.graduados.services.impl;

import com.unju.graduados.model.AnuncioEnviados;
import com.unju.graduados.repositories.IAnuncioEnviadosRepository;
import com.unju.graduados.repositories.IGraduadoRepository;
import com.unju.graduados.services.ICorreoService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class CorreoServiceImpl implements ICorreoService {

    private static final Logger logger = LoggerFactory.getLogger(CorreoServiceImpl.class);

    // Dependencias Inyectadas (Final para inmutabilidad)
    private final JavaMailSender mailSender;
    private final IAnuncioEnviadosRepository anuncioEnviadosRepository;
    private final IGraduadoRepository graduadoRepository;
    private final TemplateEngine templateEngine; // 👈 Agregamos el motor de plantillas

    // Constructor con Inyección de Dependencias
    public CorreoServiceImpl(
            @Qualifier("announcerMailSender") JavaMailSender mailSender,
            IAnuncioEnviadosRepository anuncioEnviadosRepository,
            IGraduadoRepository graduadoRepository,
            TemplateEngine templateEngine) { // 👈 Inyectado aquí

        this.mailSender = mailSender;
        this.anuncioEnviadosRepository = anuncioEnviadosRepository;
        this.graduadoRepository = graduadoRepository;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    public void enviarAnuncioAGraduadosAsync(Long anuncioId, List<Long> carrerasTarget, String tituloAnuncio, String contenidoAnuncio) {

        List<Object[]> destinatarios = graduadoRepository.findIdDatosAcademicosAndEmailsByCarreraIds(carrerasTarget);
        for (Object[] destinatario : destinatarios) {
            Long idUsuarioDatosAcademicos = (Long) destinatario[0];
            String email = (String) destinatario[1];

            try {
                // 1. Preparar el contexto de Thymeleaf para la plantilla
                Context context = new Context();
                context.setVariable("titulo", tituloAnuncio);
                context.setVariable("contenido", contenidoAnuncio);

                String htmlFinal = templateEngine.process("anuncios/template-email", context);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // multipart = true

                helper.setFrom("graduados@unju.edu.ar");
                helper.setTo(email);
                helper.setSubject("📣 UNJu Graduados: " + tituloAnuncio);
                helper.setText(htmlFinal, true);

                // --- INCRUSTAR LOGO ---
                // Buscamos la imagen en resources/static/images/logo.png
                ClassPathResource imageResource = new ClassPathResource("static/images/unju.png");
                helper.addInline("logoUnju", imageResource);
                // ----------------------

                mailSender.send(message);

                guardarEstadoEnvio(anuncioId, idUsuarioDatosAcademicos, "ENVIADO");
                logger.info("✅ ÉXITO: Registro guardado para {}", email);

            } catch (Exception e) {
                logger.error("❌ FALLO al enviar a {}: {}", email, e.getMessage());
                guardarEstadoEnvio(anuncioId, idUsuarioDatosAcademicos, "ERROR");
            }

            try {
                // Pausa de 12 segundos para cumplir con el rate limit de Mailtrap
                Thread.sleep(12000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void guardarEstadoEnvio(Long anuncioId, Long idUsuario, String estado) {
        AnuncioEnviados registro = new AnuncioEnviados();
        registro.setIdAnuncio(anuncioId);
        registro.setIdUsuarioDatosAcademicos(idUsuario);
        registro.setFechaEnvio(new java.sql.Timestamp(System.currentTimeMillis()));
        registro.setEstado(estado);
        anuncioEnviadosRepository.save(registro);
    }
}
