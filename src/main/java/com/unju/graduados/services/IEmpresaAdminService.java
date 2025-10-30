package com.unju.graduados.services;

import com.unju.graduados.dto.AltaEmpresaAdminDTO;
import com.unju.graduados.dto.EditarEmpresaAdminDTO;
import com.unju.graduados.repositories.projections.EmpresaInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEmpresaAdminService {
    // --- Métodos de CRUD de Administración ---
    void registrarAltaInternaAnunciante(AltaEmpresaAdminDTO dto);
    EditarEmpresaAdminDTO obtenerAnuncianteParaEdicion(Long id);
    void actualizarAnunciante(Long id, EditarEmpresaAdminDTO dto);

    // --- Métodos de Búsqueda y Listado (Exclusivos para el Admin) ---
    Page<EmpresaInfoProjection> findByNombreEmpresaContainingIgnoreCase(String nombre, Pageable pageable);
    Page<EmpresaInfoProjection> findByDniContaining(String dni, Pageable pageable);
    Page<EmpresaInfoProjection> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<EmpresaInfoProjection> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<EmpresaInfoProjection> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);

    Page<EmpresaInfoProjection> findByCuitContaining(String cuit, Pageable pageable);
    // Este método es el que usaste en la vista HTML, lo mantenemos como Razón Social:
    Page<EmpresaInfoProjection> findByRazonSocialContainingIgnoreCase(String razonSocial, Pageable pageable);

    // Listado general
    Page<EmpresaInfoProjection> findAllAnunciantes(Pageable pageable);

    // Eliminación (la lógica reside en la base, pero el método de acceso es de admin)
    void deleteById(Long id);
}