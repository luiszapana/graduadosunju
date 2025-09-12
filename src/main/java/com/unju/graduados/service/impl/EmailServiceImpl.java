package com.unju.graduados.service.impl;

import com.unju.graduados.service.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {
    @Override
    public void sendVerificationEmail(String to, String token) {
        // Simulación: logueamos la URL de verificación
        String url = String.format("http://localhost:8080/registro/verificar?token=%s", token);
        log.info("[EMAIL] Enviar verificación a {} -> {}", to, url);
    }
}
