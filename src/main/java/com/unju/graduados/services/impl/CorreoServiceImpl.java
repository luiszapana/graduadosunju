package com.unju.graduados.services.impl;

import com.unju.graduados.model.AnuncioEnviados;
import com.unju.graduados.repositories.IAnuncioEnviadosRepository;
import com.unju.graduados.repositories.IGraduadoRepository;
import com.unju.graduados.services.ICorreoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorreoServiceImpl implements ICorreoService {

    private static final Logger logger = LoggerFactory.getLogger(CorreoServiceImpl.class);
    private final JavaMailSender mailSender;
    private final IAnuncioEnviadosRepository anuncioEnviadosRepository;
    private final IGraduadoRepository graduadoRepository;

    @Override
    @Async
    @Transactional(readOnly = true)
    public void enviarAnuncioAGraduadosAsync(Long anuncioId, List<Long> carrerasTarget, String tituloAnuncio) {
        logger.info("Iniciando envío asíncrono del anuncio ID: {} a las carreras: {}", anuncioId, carrerasTarget);
        // 1. OBTENER ID de Datos Académicos e EMAILS de Destino
        List<Object[]> destinatarios = graduadoRepository.findIdDatosAcademicosAndEmailsByCarreraIds(carrerasTarget);
        logger.info("Encontrados {} destinatarios para el anuncio ID: {}", destinatarios.size(), anuncioId);

        // 2. ITERAR, ENVIAR y REGISTRAR
        for (Object[] destinatario : destinatarios) {
            Long idUsuarioDatosAcademicos = (Long) destinatario[0];
            String email = (String) destinatario[1];
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("no-reply@unjugraduados.com.ar");
                message.setTo(email);
                message.setSubject("📣 Nuevo Anuncio de UNJu Graduados: " + tituloAnuncio);
                message.setText("/* ... Contenido del correo ... */");
                mailSender.send(message);

                // 3. REGISTRAR EL ENVÍO
                AnuncioEnviados registro = AnuncioEnviados.builder()
                        .idAnuncio(anuncioId)
                        .idUsuarioDatosAcademicos(idUsuarioDatosAcademicos)
                        .fechaEnvio(new Timestamp(System.currentTimeMillis()))
                        .estado("ENVIADO")
                        .build();
                anuncioEnviadosRepository.save(registro);
            } catch (Exception e) {
                logger.error("Error al enviar/registrar el correo para el anuncio ID {} a {}: {}", anuncioId, email, e.getMessage());
            }
        }
    }
}
