package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ILocalidadDao extends JpaRepository<Localidad, Long> {
    Optional<Localidad> findByNombre(String nombre);
}
