package com.unju.graduados.model.repositories;

import com.unju.graduados.model.UsuarioDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioDireccionRepository extends JpaRepository<UsuarioDireccion, Long> {

}