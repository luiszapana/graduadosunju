package com.unju.graduados.dto.projections;

public record UsuarioInfoProjectionDTO(
        Long id,
        String dni,
        String apellido,
        String nombre,
        String celular,
        String email,
        // Este campo adicional sigue estando aquí.
        Boolean tituloVerificado
) {
    // NOTA: Eliminamos 'implements UsuarioInfoProjection'
}