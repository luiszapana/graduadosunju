package com.unju.graduados.repositories;

// Repositorio creado con el fin de renderizar de manera correcta la vista de graduados sin incluir el campo imagen.
public interface IUsuarioInfo {
    Long id();
    String dni();
    String apellido();
    String nombre();
    String celular();
    String email();
    // Nota: 'tituloVerificado()' no es necesario aquí si solo se usa en el DTO y no en otras proyecciones/entidades.
    // Si la interfaz solo define el contrato mínimo de la entidad Usuario, este es el cambio.
}
