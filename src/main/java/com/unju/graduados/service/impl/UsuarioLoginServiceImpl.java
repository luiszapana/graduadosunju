package com.unju.graduados.service.impl;

import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.model.dao.interfaces.IUsuarioLoginDao;
import com.unju.graduados.service.IUsuarioLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioLoginServiceImpl implements IUsuarioLoginService {
    private final IUsuarioLoginDao usuarioLoginDao;

    @Override
    public Optional<UsuarioLogin> findByUsuario(String usuario) {
        return usuarioLoginDao.findByUsuario(usuario);
    }

    @Override
    public UsuarioLogin save(UsuarioLogin login) {
        return usuarioLoginDao.save(login);
    }
}
