package com.unju.graduados.services;

import com.unju.graduados.repositories.IUsuarioInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IGraduadoService extends IUsuarioBaseService{
    Page<IUsuarioInfo> findByDniContaining(String dni, Pageable pageable);
    Page<IUsuarioInfo> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<IUsuarioInfo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<IUsuarioInfo> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);
    Page<IUsuarioInfo> findByFacultadNombreContainingIgnoreCase(String nombreFacultad, Pageable pageable);
    Page<IUsuarioInfo> findByCarreraNombreContainingIgnoreCase(String nombreCarrera, Pageable pageable);
    Page<IUsuarioInfo> findAllGraduados(Pageable pageable);
}
