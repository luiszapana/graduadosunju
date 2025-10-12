package com.unju.graduados.services;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.repositories.IUsuarioInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);

    Page<IUsuarioInfo> findByDniContaining(String dni, Pageable pageable);
    Page<IUsuarioInfo> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<IUsuarioInfo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<IUsuarioInfo> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);
    Page<IUsuarioInfo> findByFacultadNombreContainingIgnoreCase(String nombreFacultad, Pageable pageable);
    Page<IUsuarioInfo> findByCarreraNombreContainingIgnoreCase(String nombreCarrera, Pageable pageable);

    List<Usuario> findAll();
    Page<IUsuarioInfo> findAllGraduados(Pageable pageable);
    void deleteById(Long id);
}
