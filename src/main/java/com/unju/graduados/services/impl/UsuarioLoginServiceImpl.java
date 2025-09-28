package com.unju.graduados.services.impl;

import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.model.repositories.IUsuarioLoginRepository;
import com.unju.graduados.services.IUsuarioLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioLoginServiceImpl implements IUsuarioLoginService {
    private final IUsuarioLoginRepository usuarioLoginDao;

    @Override
    public Optional<UsuarioLogin> findByUsuario(String usuario) {
        return usuarioLoginDao.findByUsuario(usuario);
    }

    @Override
    public UsuarioLogin save(UsuarioLogin login) {
        return usuarioLoginDao.save(login);
    }
}
