package com.unju.graduados.services.impl;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.repositories.IUsuarioRepository; // <-- Nuevo Repositorio
import com.unju.graduados.services.IUsuarioBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioBaseServiceImpl implements IUsuarioBaseService {

    private final IUsuarioRepository usuarioRepository;

    // Implementación del método clave para el controlador
    @Override
    public Optional<Usuario> findByNombreLogin(String nombreLogin) {
        // Asumiendo que el 'nombreLogin' del Principal es el 'email' del usuario
        return usuarioRepository.findByEmail(nombreLogin);
    }

    // Implementación de los otros métodos de IUsuarioBaseService

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
}