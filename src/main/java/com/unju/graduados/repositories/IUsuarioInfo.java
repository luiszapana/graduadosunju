package com.unju.graduados.repositories;

// Repositorio creado con el fin de renderizar de manera correcta la vista de graduados sin incluir el campo imagen.
public interface IUsuarioInfo {
    Long id();
    String dni();
    String apellido();
    String nombre();
    String celular();
    String email();
}
