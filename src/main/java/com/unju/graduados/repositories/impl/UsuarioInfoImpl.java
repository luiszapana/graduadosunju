package com.unju.graduados.repositories.impl;

import com.unju.graduados.model.Usuario;
import com.unju.graduados.repositories.IUsuarioInfo;

// Esta clase implementa UsuarioInfo y usa la entidad Usuario para obtener los datos.
public record UsuarioInfoImpl(Usuario u) implements IUsuarioInfo {

    @Override
    public Long getId() {
        return u.getId();
    }
    @Override
    public String getDni() {
        return u.getDni();
    }
    @Override
    public String getApellido() {
        return u.getApellido();
    }
    @Override
    public String getNombre() {
        return u.getNombre();
    }
    @Override
    public String getCelular() {
        return u.getCelular();
    }
    @Override
    public String getEmail() {
        return u.getEmail();
    }
}
