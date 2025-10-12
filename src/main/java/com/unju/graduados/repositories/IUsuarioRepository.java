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

    // Métodos para obtener una entidad completa (e.g., para editar)
    Optional<Usuario> findByEmail(String email);
    Page<IUsuarioInfo> findByDni(String dni, Pageable pageable);

    // Métodos para la tabla de administración (Proyecciones)
    // Estos resuelven el error de BYTEA a long al no seleccionar la columna imagen
    Page<IUsuarioInfo> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<IUsuarioInfo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<IUsuarioInfo> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);

    @Query("SELECT u.id as id, u.dni as dni, u.apellido as apellido, u.nombre as nombre, u.celular as celular, u.email as email FROM Usuario u")
    Page<IUsuarioInfo> findAllGraduados(Pageable pageable);

    @Query("""
    SELECT 
        u.id AS id,
        u.dni AS dni,
        u.apellido AS apellido,
        u.nombre AS nombre,
        u.celular AS celular,
        u.email AS email
    FROM Usuario u
    JOIN UsuarioDatosAcademicos da ON da.usuario = u
    JOIN Facultad f ON f.id = da.facultad.id
    WHERE LOWER(f.etiqueta) LIKE LOWER(CONCAT('%', :nombreFacultad, '%'))
    """)
    Page<IUsuarioInfo> findByFacultadNombreContainingIgnoreCase(@Param("nombreFacultad") String nombreFacultad, Pageable pageable);

    @Query("""
    SELECT u.id as id, u.dni as dni, u.apellido as apellido, u.nombre as nombre, u.celular as celular, u.email as email
    FROM Usuario u
    JOIN UsuarioDatosAcademicos da ON da.usuario = u
    JOIN da.carrera c
    WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombreCarrera, '%'))
    """)
    Page<IUsuarioInfo> findByCarreraNombreContainingIgnoreCase(@Param("nombreCarrera") String nombreCarrera, Pageable pageable);
}
