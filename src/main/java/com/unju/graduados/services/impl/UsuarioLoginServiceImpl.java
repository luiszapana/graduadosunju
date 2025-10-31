package com.unju.graduados.services.impl;

import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.repositories.IUsuarioLoginRepository;
import com.unju.graduados.services.IUsuarioLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioLoginServiceImpl implements IUsuarioLoginService {
    private final IUsuarioLoginRepository usuarioLoginRepository;

    @Override
    public Optional<UsuarioLogin> findByUsuario(String usuario) {
        return usuarioLoginRepository.findByUsuario(usuario);
    }

    @Override
    public UsuarioLogin save(UsuarioLogin login) {
        return usuarioLoginRepository.save(login);
    }

    @Override
    public Optional<UsuarioLogin> findByIdUsuario(Long usuarioId) {
        // Utilizamos el método del repositorio que trae los perfiles cargados
        return usuarioLoginRepository.findByIdUsuarioConPerfiles(usuarioId);
    }
}
