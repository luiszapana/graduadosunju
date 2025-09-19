package com.unju.graduados.controllers;

import com.unju.graduados.dto.UsuarioLoginDTO;
import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.service.IUsuarioLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UsuarioLoginRestController {

    private final IUsuarioLoginService usuarioLoginService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDTO dto) {
        // Stub simple: valida credenciales con Spring Security a través de formulario (no JWT por ahora)
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody UsuarioLoginDTO dto) {
        UsuarioLogin login = UsuarioLogin.builder()
                .usuario(dto.getUsuario())
                .password(passwordEncoder.encode(dto.getPassword()))
                .habilitado(true)
                .build();
        usuarioLoginService.save(login);
        return ResponseEntity.ok().build();
    }
}
