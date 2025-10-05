package com.unju.graduados.services.impl;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.repositories.*;
import com.unju.graduados.services.IUsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioLoginRepository usuarioLoginRepository;
    private final IUsuarioDireccionRepository usuarioDireccionRepository;
    private final IUsuarioDatosAcademicosRepository usuarioDatosAcademicosRepository;
    private final IUsuarioLoginPerfilesRepository usuarioLoginPerfilesRepository;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Optional<Usuario> findByDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Page<IUsuarioInfo> findAllGraduados(Pageable pageable) {
        // Llama al nuevo método del repositorio
        return usuarioRepository.findAllGraduados(pageable);
    }

    /**
     * Elimina un Usuario y todas sus dependencias en el orden requerido
     * por las restricciones de clave foránea de la base de datos.
     */
    @Override
    @Transactional
    public void deleteById(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // 1️⃣ Buscar login asociado (no está mapeado, así que hay que hacer query)
        UsuarioLogin usuarioLogin = usuarioLoginRepository.findByIdUsuario(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró login para el usuario ID: " + usuarioId));

        Long loginId = usuarioLogin.getId();

        // 2️⃣ Eliminar perfiles asociados al login
        usuarioLoginPerfilesRepository.deleteByLoginId(loginId);

        // 3️⃣ Eliminar login
        usuarioLoginRepository.deleteById(loginId);

        // 4️⃣ Manejo explícito de relaciones OneToOne antes de eliminar Usuario
        // Evita problemas de FK si Hibernate no ha cargado los hijos en el contexto.
        if (usuario.getDatosAcademicos() != null) {
            usuarioDatosAcademicosRepository.delete(usuario.getDatosAcademicos());
            usuario.setDatosAcademicos(null);
        }

        /*if (usuario.getDatosEmpresa() != null) {
            usuarioDatosEmpresaRepository.delete(usuario.getDatosEmpresa());
            usuario.setDatosEmpresa(null);
        }*/

        if (usuario.getDireccion() != null) {
            usuarioDireccionRepository.delete(usuario.getDireccion());
            usuario.setDireccion(null);
        }

        // 5️⃣ Finalmente eliminar usuario
        usuarioRepository.delete(usuario);
    }


    @Override
    public Page<IUsuarioInfo> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
        // El repositorio ahora devuelve Page<UsuarioInfo>, ¡lo cual es correcto!
        return usuarioRepository.findByEmailContainingIgnoreCase(email, pageable);
    }

    @Override
    public Page<IUsuarioInfo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public Page<IUsuarioInfo> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable) {
        return usuarioRepository.findByApellidoContainingIgnoreCase(apellido, pageable);
    }
}
