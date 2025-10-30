package com.unju.graduados.repositories.projections;

public interface EmpresaInfoProjection {

    // De la tabla 'usuario'
    Long getId();             // Identificador principal (para editar/eliminar)
    String getDni();
    String getApellido();
    String getNombre();
    String getCelular();
    String getEmail();

    // De la tabla 'usuario_login' (o donde almacenes los perfiles)
    String getPerfiles();     // Asumiendo que esta información es crucial para la lista

    // De la tabla 'usuario_datos_empresa'
    String getCuit();
    String getRazonSocial();
}
