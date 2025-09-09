package com.unju.graduados.service.impl;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.dao.interfaces.IUsuarioDao;
import com.unju.graduados.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioDao usuarioDao;

    @Autowired
    public UsuarioServiceImpl(IUsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioDao.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioDao.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioDao.findByEmail(email);
    }

    @Override
    public Optional<Usuario> findByDni(Long dni) {
        return usuarioDao.findByDni(dni);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioDao.findAll();
    }

    @Override
    public void deleteById(Long id) {
        usuarioDao.deleteById(id);
    }
}
