package com.unju.graduados.controllers;

import com.unju.graduados.service.IUsuarioLoginService;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recuperar")
public class RecuperacionController {

    private final IUsuarioLoginService usuarioLoginService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    @GetMapping
    public String form(Model model) {
        model.addAttribute("dto", new RequestDTO());
        return "recuperar/form";
    }

    @PostMapping
    public String send(@Valid @ModelAttribute("dto") RequestDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) return "recuperar/form";
        var opt = usuarioLoginService.findByUsuario(dto.getEmail());
        if (opt.isEmpty()) {
            model.addAttribute("message", "Si el email existe, se enviará un enlace de recuperación");
            return "recuperar/ok";
        }
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenInfo(dto.getEmail(), Instant.now().plus(1, ChronoUnit.HOURS)));
        String link = "/recuperar/reset?token=" + token;

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(dto.getEmail());
            msg.setSubject("Recuperación de contraseña");
            msg.setText("Use el siguiente enlace para restablecer su contraseña (válido por 1 hora): " + link);
            mailSender.send(msg);
        } catch (Exception e) {
            System.out.println("[MAIL SIMULADO] Enlace de recuperación: " + link);
        }
        model.addAttribute("message", "Se envió un enlace de recuperación si el email existe");
        return "recuperar/ok";
    }

    @GetMapping("/reset")
    public String resetForm(@RequestParam String token, Model model) {
        TokenInfo info = tokens.get(token);
        if (info == null || Instant.now().isAfter(info.expiresAt)) {
            model.addAttribute("message", "Token inválido o expirado");
            return "recuperar/ok";
        }
        model.addAttribute("token", token);
        model.addAttribute("dto", new ResetDTO());
        return "recuperar/reset";
    }

    @PostMapping("/reset")
    public String doReset(@RequestParam String token, @Valid @ModelAttribute("dto") ResetDTO dto, BindingResult result, Model model) {
        TokenInfo info = tokens.get(token);
        if (info == null || Instant.now().isAfter(info.expiresAt)) {
            model.addAttribute("message", "Token inválido o expirado");
            return "recuperar/ok";
        }
        if (result.hasErrors()) return "recuperar/reset";
        var login = usuarioLoginService.findByUsuario(info.email).orElse(null);
        if (login == null) {
            model.addAttribute("message", "Usuario no encontrado");
            return "recuperar/ok";
        }
        login.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuarioLoginService.save(login);
        tokens.remove(token);
        model.addAttribute("message", "Contraseña actualizada");
        return "recuperar/ok";
    }

    @Data
    public static class RequestDTO {
        @Email
        private String email;
    }

    @Data
    public static class ResetDTO {
        @jakarta.validation.constraints.Size(min = 8)
        private String password;
    }

    private record TokenInfo(String email, Instant expiresAt) {}
}
