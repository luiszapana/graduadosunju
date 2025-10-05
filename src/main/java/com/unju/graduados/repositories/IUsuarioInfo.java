package com.unju.graduados.repositories;

// Archivo: com.unju.graduados.repository.UsuarioInfo.java

public interface IUsuarioInfo {
    //Esta interfaz fue creada con el fin de renderizar de manera correcta la vista de graudados sin incluir el campo imagen.
    // Es CRUCIAL añadir el ID si se usa en la vista para enlaces/acciones.
    Long getId();

    String getDni();
    String getApellido();
    String getNombre();
    String getCelular();
    String getEmail();
}
