package com.unju.graduados.repositories;

import java.time.ZonedDateTime;

public interface IUsuarioSinImagen {
    Long getId();
    String getDni();
    String getApellido();
    String getNombre();
    ZonedDateTime getFechaNacimiento();
    String getEmail();
    String getTelefono();
    String getCelular();
}
