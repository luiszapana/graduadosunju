package com.unju.graduados.repositories;

import com.unju.graduados.model.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;


@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    // Métodos para obtener una entidad completa (e.g., para editar)
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDni(String dni);

    // Métodos para la tabla de administración (Proyecciones)
    // Estos resuelven el error de BYTEA a long al no seleccionar la columna imagen
    Page<IUsuarioInfo> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<IUsuarioInfo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<IUsuarioInfo> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);

    @Query("SELECT u.id as id, u.dni as dni, u.apellido as apellido, u.nombre as nombre, u.celular as celular, u.email as email FROM Usuario u")
    Page<IUsuarioInfo> findAllGraduados(Pageable pageable);
    //Page<IUsuarioInfo> findAll(Pageable pageable);
}
