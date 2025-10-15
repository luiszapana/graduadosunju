package com.unju.graduados.dto;

import com.unju.graduados.repositories.IUsuarioInfo;

public record UsuarioInfoDTO(
        Long id,
        String dni,
        String apellido,
        String nombre,
        String celular,
        String email,
        // Este campo es adicional al DTO, pero necesario para la proyección de la vista de graduados.
        Boolean tituloVerificado
) implements IUsuarioInfo {

}