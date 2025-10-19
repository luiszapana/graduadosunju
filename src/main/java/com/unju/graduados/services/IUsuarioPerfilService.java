package com.unju.graduados.services;

import com.unju.graduados.dto.UsuarioPerfilDto;
import com.unju.graduados.repositories.projections.UsuarioInfoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUsuarioPerfilService {

    /**
     * Obtiene una página de usuarios (solo con su información básica) que
     * tienen el perfilId especificado.
     * * NOTA: Este método se usará para el listado, por lo que usa la proyección IUsuarioInfo.
     * La lógica del servicio deberá asegurar que se excluyan los usuarios que
     * solo tienen el perfil GRADUADO (ID 4) si el perfilId es nulo o si se requiere un filtro específico.
     * * @param perfilId El ID del perfil a filtrar (1: USUARIO, 2: MODERADOR, 3: ADMINISTRADOR). Puede ser nulo.
     * @param pageable La información de paginación.
     * @return Una página de objetos ligeros IUsuarioInfo.
     */
    Page<UsuarioInfoProjection> findUsuariosByPerfilId(Long perfilId, Pageable pageable);

    /**
     * Obtiene los datos completos de un usuario, junto con el estado de sus perfiles
     * (es decir, qué perfiles tiene asignados y cuáles no).
     * * @param usuarioId El ID del usuario a editar.
     * @return Un DTO (Data Transfer Object) para la edición de perfiles.
     */
    UsuarioPerfilDto getUsuarioPerfiles(Long usuarioId);

    /**
     * Actualiza la lista de perfiles asignados a un usuario, reemplazando la lista
     * actual con los IDs de perfil proporcionados.
     * * @param usuarioId El ID del usuario a modificar.
     * @param perfilIds La nueva lista de IDs de perfil que debe tener el usuario.
     */
    void updateUsuarioPerfiles(Long usuarioId, List<Long> perfilIds);
}
