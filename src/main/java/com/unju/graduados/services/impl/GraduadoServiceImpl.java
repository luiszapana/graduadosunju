package com.unju.graduados.services.impl;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.repositories.*;
import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import com.unju.graduados.services.IGraduadoService;
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
public class GraduadoServiceImpl implements IGraduadoService {

    private final IGraduadoRepository usuarioRepository;
    private final IUsuarioLoginRepository usuarioLoginRepository;
    private final IUsuarioDireccionRepository usuarioDireccionRepository;
    private final IUsuarioDatosAcademicosRepository usuarioDatosAcademicosRepository;
    private final IUsuarioLoginPerfilesRepository usuarioLoginPerfilesRepository;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * BUSQUEDAS
     */

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca graduados por DNI utilizando proyección IUsuarioInfo.
     * Repo creado con el fin de renderizar de manera correcta la vista de graduados sin incluir el campo imagen..
     */
    @Override
    public Page<UsuarioInfoProjection> findByDniContaining(String dni, Pageable pageable) {
        // Asumiendo que has añadido el método findByDni al repositorio
        // y que este método en el repositorio devuelve la proyección Page<IUsuarioInfo>
        return usuarioRepository.findByDni(dni, pageable);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Page<UsuarioInfoProjection> findAllGraduados(Pageable pageable) {
        return usuarioRepository.findAllGraduados(pageable);
    }

    @Override
    public Page<UsuarioInfoProjection> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
        return usuarioRepository.findByEmailContainingIgnoreCase(email, pageable);
    }

    @Override
    public Page<UsuarioInfoProjection> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public Page<UsuarioInfoProjection> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable) {
        return usuarioRepository.findByApellidoContainingIgnoreCase(apellido, pageable);
    }

    @Override
    public Page<UsuarioInfoProjection> findByFacultadNombreContainingIgnoreCase(String nombreFacultad, Pageable pageable) {
        return usuarioRepository.findByFacultadNombreContainingIgnoreCase(nombreFacultad, pageable);
    }

    @Override
    public Page<UsuarioInfoProjection> findByCarreraNombreContainingIgnoreCase(String nombreCarrera, Pageable pageable) {
        return usuarioRepository.findByCarreraNombreContainingIgnoreCase(nombreCarrera, pageable);
    }

    /**
     * Elimina un Usuario y todas sus dependencias en el orden requerido
     * por las restricciones de clave foránea de la base de datos.
     */
    @Override
    @Transactional
    public void deleteById(Long usuarioId) {
        // 0. El usuario principal DEBE existir para continuar
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // --- Borrado de dependencias (Refactorizado con Referencias a Métodos) ---

        // 1. Eliminar Datos Académicos (Si existe)
        usuarioDatosAcademicosRepository.findByIdUsuario(usuarioId)
                .ifPresent(usuarioDatosAcademicosRepository::delete); // <-- ¡Referencia a método aplicada!

        // 2. Eliminar Dirección (Si existe)
        usuarioDireccionRepository.findByIdUsuario(usuarioId)
                .ifPresent(usuarioDireccionRepository::delete); // <-- ¡Referencia a método aplicada!

        // 3. Eliminar Login y sus Perfiles (Si existe)
        usuarioLoginRepository.findByIdUsuario(usuarioId)
                .ifPresent(usuarioLogin -> {
                    // A. Eliminar perfiles asociados al login (usando la clave ID del login)
                    Long loginId = usuarioLogin.getId();
                    // Asumo que tienes el repositorio para la tabla intermedia o el método custom
                    usuarioLoginPerfilesRepository.deleteByLoginId(loginId);

                    // B. Eliminar login
                    usuarioLoginRepository.deleteById(loginId);
                });

        // 4. Finalmente eliminar usuario raíz
        usuarioRepository.delete(usuario);
    }

    /**
     * Implementación requerida por IUsuarioBaseService.
     * Busca un Usuario por su nombre de login (que es el campo email).
     */
    @Override
    public Optional<Usuario> findByNombreLogin(String nombreLogin) {
        // Asumiendo que tu repositorio tiene un método findByEmail
        return usuarioRepository.findByEmail(nombreLogin);
    }
}
