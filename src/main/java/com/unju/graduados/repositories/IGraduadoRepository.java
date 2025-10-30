package com.unju.graduados.repositories;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import com.unju.graduados.repositories.projections.UsuarioSinImagenProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;

@Repository
public interface IGraduadoRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT new com.unju.graduados.dto.projections.UsuarioInfoProjectionDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " + // 1. Conexión a UsuarioLogin
            "JOIN ul.perfiles p " + // 2. Conexión a la colección de Perfiles
            "WHERE p.id = 4 AND " + // 3. FILTRO PRINCIPAL: Solo GRADUADOS (ID 4)
            "u.dni LIKE CONCAT('%', :dni, '%')")
    Page<UsuarioInfoProjection> findByDni(@Param("dni") String dni, Pageable pageable);

    @Query(value = "SELECT new com.unju.graduados.dto.projections.UsuarioInfoProjectionDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " + // 1. Conexión a UsuarioLogin
            "JOIN ul.perfiles p " + // 2. Conexión a la colección de Perfiles
            "WHERE p.id = 4 AND " + // 3. FILTRO PRINCIPAL: Solo GRADUADOS (ID 4)
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
            "ORDER BY u.apellido ASC")
    Page<UsuarioInfoProjection> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);

    @Query(value = "SELECT new com.unju.graduados.dto.projections.UsuarioInfoProjectionDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " + // 1. Conexión a UsuarioLogin
            "JOIN ul.perfiles p " + // 2. Conexión a la colección de Perfiles
            "WHERE p.id = 4 AND " + // 3. FILTRO PRINCIPAL: Solo GRADUADOS (ID 4)
            "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))")
    Page<UsuarioInfoProjection> findByApellidoContainingIgnoreCase(@Param("apellido") String apellido, Pageable pageable);

    @Query(value = "SELECT new com.unju.graduados.dto.projections.UsuarioInfoProjectionDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " + // 1. Conexión a UsuarioLogin
            "JOIN ul.perfiles p " + // 2. Conexión a la colección de Perfiles
            "WHERE p.id = 4 AND " + // 3. FILTRO PRINCIPAL: Solo GRADUADOS (ID 4)
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) " +
            "ORDER BY u.apellido ASC")
    Page<UsuarioInfoProjection> findByEmailContainingIgnoreCase(@Param("email") String email, Pageable pageable);

    /**
     * Trae solo a los usuarios que tienen asociado el perfil 'GRADUADO' (ID 4),
     * utilizando la relación @ManyToMany mapeada en UsuarioLogin.
     */
    @Query(value = "SELECT new com.unju.graduados.dto.projections.UsuarioInfoProjectionDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " + // 1. Conexión a UsuarioLogin
            "JOIN ul.perfiles p " + // 2. ¡USAMOS LA PROPIEDAD 'perfiles' DE UsuarioLogin!
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " + // Datos Académicos
            "WHERE p.id = 4 " +
            "ORDER BY u.apellido ASC")
    Page<UsuarioInfoProjection> findAllGraduados(Pageable pageable);

    @Query(value = "SELECT new com.unju.graduados.dto.projections.UsuarioInfoProjectionDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " + // ⬅️ DTO y campos
            "FROM Usuario u " +
            "JOIN UsuarioDatosAcademicos da ON u.id = da.idUsuario " + // JOIN a Datos Académicos
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " + // 1. Conexión a UsuarioLogin
            "JOIN ul.perfiles p " + // 2. Conexión a la colección de Perfiles
            "WHERE p.id = 4 AND " + // 3. FILTRO PRINCIPAL: Solo GRADUADOS (ID 4)
            "LOWER(da.facultad.etiqueta) LIKE LOWER(CONCAT('%', :nombreFacultad, '%')) " +
            "ORDER BY u.apellido ASC")
    Page<UsuarioInfoProjection> findByFacultadNombreContainingIgnoreCase(@Param("nombreFacultad") String nombreFacultad, Pageable pageable);

    @Query(value = "SELECT new com.unju.graduados.dto.projections.UsuarioInfoProjectionDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " + // ⬅️ DTO y campos
            "FROM Usuario u " +
            "JOIN UsuarioDatosAcademicos da ON u.id = da.idUsuario " + // JOIN a Datos Académicos
            "JOIN UsuarioLogin ul ON ul.idUsuario = u.id " + // 1. Conexión a UsuarioLogin
            "JOIN ul.perfiles p " + // 2. Conexión a la colección de Perfiles
            "WHERE p.id = 4 AND " + // 3. FILTRO PRINCIPAL: Solo GRADUADOS (ID 4)
            "LOWER(da.carrera.nombre) LIKE LOWER(CONCAT('%', :nombreCarrera, '%')) " +
            "ORDER BY u.apellido ASC")
    Page<UsuarioInfoProjection> findByCarreraNombreContainingIgnoreCase(@Param("nombreCarrera") String nombreCarrera, Pageable pageable);

    // Obtener graduado para edición
    @Query("""
    SELECT u.id AS id, u.dni AS dni, u.apellido AS apellido, u.nombre AS nombre, u.fechaNacimiento AS fechaNacimiento,
           u.email AS email, u.telefono AS telefono, u.celular AS celular
    FROM Usuario u WHERE u.id = :id
    """)
    Optional<UsuarioSinImagenProjection> findProjectedById(@Param("id") Long id);

    // Método para verificar si un DNI ya existe
    boolean existsByDni(String dni);

    // Método para verificar si un Email ya existe
    boolean existsByEmail(String email);
}
