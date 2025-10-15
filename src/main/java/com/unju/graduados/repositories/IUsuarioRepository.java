package com.unju.graduados.repositories;

import com.unju.graduados.model.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    @Query(value = "SELECT new com.unju.graduados.dto.UsuarioInfoDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "WHERE u.dni LIKE CONCAT('%', :dni, '%')") // Búsqueda por DNI
    Page<IUsuarioInfo> findByDni(@Param("dni") String dni, Pageable pageable);
    @Query(value = "SELECT new com.unju.graduados.dto.UsuarioInfoDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<IUsuarioInfo> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);
    @Query(value = "SELECT new com.unju.graduados.dto.UsuarioInfoDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "WHERE LOWER(u.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))")
    Page<IUsuarioInfo> findByApellidoContainingIgnoreCase(@Param("apellido") String apellido, Pageable pageable);
    @Query(value = "SELECT new com.unju.graduados.dto.UsuarioInfoDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id " +
            "WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<IUsuarioInfo> findByEmailContainingIgnoreCase(@Param("email") String email, Pageable pageable);
    @Query(value = "SELECT new com.unju.graduados.dto.UsuarioInfoDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " +
            "FROM Usuario u " +
            "LEFT JOIN UsuarioDatosAcademicos da ON da.idUsuario = u.id") // JOIN por clave foránea Long
    Page<IUsuarioInfo> findAllGraduados(Pageable pageable);
    @Query(value = "SELECT new com.unju.graduados.dto.UsuarioInfoDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " + // ⬅️ DTO y campos
            "FROM Usuario u " +
            "JOIN UsuarioDatosAcademicos da ON u.id = da.idUsuario " + // JOIN manual
            "WHERE LOWER(da.facultad.etiqueta) LIKE LOWER(CONCAT('%', :nombreFacultad, '%'))")
    Page<IUsuarioInfo> findByFacultadNombreContainingIgnoreCase(@Param("nombreFacultad") String nombreFacultad, Pageable pageable);
    @Query(value = "SELECT new com.unju.graduados.dto.UsuarioInfoDTO(" +
            "u.id, u.dni, u.apellido, u.nombre, u.celular, u.email, da.tituloVerificado) " + // ⬅️ DTO y campos
            "FROM Usuario u " +
            "JOIN UsuarioDatosAcademicos da ON u.id = da.idUsuario " + // JOIN manual
            "WHERE LOWER(da.carrera.nombre) LIKE LOWER(CONCAT('%', :nombreCarrera, '%'))")
    Page<IUsuarioInfo> findByCarreraNombreContainingIgnoreCase(@Param("nombreCarrera") String nombreCarrera, Pageable pageable);
    @Query("""
        SELECT 
            u.id AS id,
            u.dni AS dni,
            u.apellido AS apellido,
            u.nombre AS nombre,
            u.fechaNacimiento AS fechaNacimiento,
            u.email AS email,
            u.telefono AS telefono,
            u.celular AS celular
        FROM Usuario u 
        WHERE u.id = :id
    """)
    Optional<IUsuarioSinImagen> findProjectedById(@Param("id") Long id);
}
