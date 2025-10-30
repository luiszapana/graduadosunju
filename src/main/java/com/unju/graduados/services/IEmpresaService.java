package com.unju.graduados.services;

import com.unju.graduados.model.UsuarioDatosEmpresa;

// Esta interfaz solo expone métodos para que un anunciante se autogestione.
public interface IEmpresaService extends IUsuarioBaseService {

    void saveDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa datosEmpresa);
}
