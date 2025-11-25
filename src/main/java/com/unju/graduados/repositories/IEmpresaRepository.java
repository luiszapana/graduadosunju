package com.unju.graduados.repositories;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.repositories.projections.EmpresaInfoProjection;
import com.unju.graduados.repositories.projections.UsuarioSinImagenProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEmpresaRepository extends JpaRepository<Usuario, Long> {

    // =========================================================================
    // CONSULTAS DE BÚSQUEDA GENERALES (FILTRANDO POR PERFIL ANUNCIANTE ID 5)
    // =========================================================================

    // 1. Búsqueda por DNI
    @Query(value = "SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, " +
            "u.celular AS celular, u.email AS email, de.cuit AS cuit, de.razonSocial AS razonSocial " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosEmpresa de ON de.idUsuario = u.id " + // 🎯 JOIN a Datos de Empresa
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " +
            "JOIN ul.perfiles p " +
            "WHERE p.id = 5 AND " + // FILTRO PRINCIPAL: Solo ANUNCIANTES (ID 5)
            "u.dni LIKE CONCAT('%', :dni, '%')")
    Page<EmpresaInfoProjection> findByDniContaining(@Param("dni") String dni, Pageable pageable);


    // 2. Búsqueda por Nombre
    @Query(value = "SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, " +
            "u.celular AS celular, u.email AS email, de.cuit AS cuit, de.razonSocial AS razonSocial " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosEmpresa de ON de.idUsuario = u.id " + // 🎯 JOIN a Datos de Empresa
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " +
            "JOIN ul.perfiles p " +
            "WHERE p.id = 5 AND " + // FILTRO PRINCIPAL: Solo ANUNCIANTES (ID 5)
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
            "ORDER BY u.apellido ASC")
    Page<EmpresaInfoProjection> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);

    // 3. Búsqueda por Apellido
    @Query(value = "SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, " +
            "u.celular AS celular, u.email AS email, de.cuit AS cuit, de.razonSocial AS razonSocial " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosEmpresa de ON de.idUsuario = u.id " + // 🎯 JOIN a Datos de Empresa
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " +
            "JOIN ul.perfiles p " +
            "WHERE p.id = 5 AND " + // 🎯 FILTRO PRINCIPAL: Solo ANUNCIANTES (ID 5)
            "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))")
    Page<EmpresaInfoProjection> findByApellidoContainingIgnoreCase(@Param("apellido") String apellido, Pageable pageable);

    // 4. Búsqueda por Email (Principal del Usuario)
    @Query(value = "SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, " +
            "u.celular AS celular, u.email AS email, de.cuit AS cuit, de.razonSocial AS razonSocial " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosEmpresa de ON de.idUsuario = u.id " + // 🎯 JOIN a Datos de Empresa
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " +
            "JOIN ul.perfiles p " +
            "WHERE p.id = 5 AND " + // 🎯 FILTRO PRINCIPAL: Solo ANUNCIANTES (ID 5)
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) " +
            "ORDER BY u.apellido ASC")
    Page<EmpresaInfoProjection> findByEmailContainingIgnoreCase(@Param("email") String email, Pageable pageable);

    // =========================================================================
    // CONSULTAS ESPECÍFICAS DE ANUNCIANTE
    // =========================================================================

    // 5. Búsqueda por CUIT
    @Query(value = "SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, " +
            "u.celular AS celular, u.email AS email, de.cuit AS cuit, de.razonSocial AS razonSocial " +
            "FROM Usuario u " +
            "JOIN UsuarioDatosEmpresa de ON de.idUsuario = u.id " + // 🎯 JOIN obligatorio para CUIT
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " +
            "JOIN ul.perfiles p " +
            "WHERE p.id = 5 AND " + // 🎯 FILTRO PRINCIPAL: Solo ANUNCIANTES (ID 5)
            "de.cuit LIKE CONCAT('%', :cuit, '%')")
    Page<EmpresaInfoProjection> findByCuitContaining(@Param("cuit") String cuit, Pageable pageable);

    // 6. Búsqueda por Razón Social
    @Query(value = "SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, " +
            "u.celular AS celular, u.email AS email, de.cuit AS cuit, de.razonSocial AS razonSocial " +
            "FROM Usuario u " +
            "JOIN UsuarioDatosEmpresa de ON de.idUsuario = u.id " + // 🎯 JOIN obligatorio para Razón Social
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " +
            "JOIN ul.perfiles p " +
            "WHERE p.id = 5 AND " + // 🎯 FILTRO PRINCIPAL: Solo ANUNCIANTES (ID 5)
            "LOWER(de.razonSocial) LIKE LOWER(CONCAT('%', :razonSocial, '%')) " +
            "ORDER BY u.apellido ASC")
    Page<EmpresaInfoProjection> findByRazonSocialContainingIgnoreCase(@Param("razonSocial") String razonSocial, Pageable pageable);


    // 7. Traer todos los ANUNCIANTES (Listado principal)
    @Query(value = "SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, " +
            "u.celular AS celular, u.email AS email, de.cuit AS cuit, de.razonSocial AS razonSocial " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosEmpresa de ON de.idUsuario = u.id " + // Datos de Empresa (LEFT JOIN)
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " +
            "JOIN ul.perfiles p " +
            "WHERE p.id = 5 " + // 🎯 FILTRO PRINCIPAL: Solo ANUNCIANTES (ID 5)
            "ORDER BY u.apellido ASC")
    Page<EmpresaInfoProjection> findAllAnunciantes(Pageable pageable);


    // =========================================================================
    // CONSULTAS AUXILIARES (Tomadas del GraduadoRepository)
    // =========================================================================

    // Usada para obtener los datos base en la edición
    @Query("""
    SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, u.fechaNacimiento AS fechaNacimiento,
           u.email AS email, u.telefono AS telefono, u.celular AS celular
    FROM Usuario u 
    WHERE u.id = :id
    """)
    Optional<UsuarioSinImagenProjection> findProjectedById(@Param("id") Long id);


    // Método para verificar si un DNI ya existe
    boolean existsByDni(String dni);

    // Método para verificar si un Email ya existe
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
