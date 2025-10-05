package com.unju.graduados.repositories;

import com.unju.graduados.model.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ILocalidadRepository extends JpaRepository<Localidad, Long> {
    Optional<Localidad> findByNombre(String nombre);
}
