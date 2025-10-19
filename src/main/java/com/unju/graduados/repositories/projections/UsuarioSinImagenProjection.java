package com.unju.graduados.repositories.projections;

import java.time.ZonedDateTime;

public interface UsuarioSinImagenProjection {
    Long getId();
    String getDni();
    String getApellido();
    String getNombre();
    ZonedDateTime getFechaNacimiento();
    String getEmail();
    String getTelefono();
    String getCelular();
}
