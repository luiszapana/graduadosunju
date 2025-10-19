package com.unju.graduados.repositories.projections;

import java.lang.Long;
import java.lang.String;

public interface UsuarioInfoProjection {
    Long getId();
    String getDni();
    String getApellido();
    String getNombre();
    String getCelular();
    String getEmail();
    String getPerfiles();

    Boolean getTituloVerificado();
}