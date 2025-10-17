package com.unju.graduados.services;

import com.unju.graduados.model.UsuarioDatosEmpresa;

public interface IAnuncianteService extends IUsuarioBaseService {
    void saveDatosEmpresa(Long usuarioId, UsuarioDatosEmpresa datosEmpresa);
}
