package com.unju.graduados.services;

import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IGraduadoService extends IUsuarioBaseService{
    Page<UsuarioInfoProjection> findByDniContaining(String dni, Pageable pageable);
    Page<UsuarioInfoProjection> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<UsuarioInfoProjection> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<UsuarioInfoProjection> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);
    Page<UsuarioInfoProjection> findByFacultadNombreContainingIgnoreCase(String nombreFacultad, Pageable pageable);
    Page<UsuarioInfoProjection> findByCarreraNombreContainingIgnoreCase(String nombreCarrera, Pageable pageable);
    Page<UsuarioInfoProjection> findAllGraduados(Pageable pageable);
}
