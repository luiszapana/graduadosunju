package com.unju.graduados.repositories;

import com.unju.graduados.model.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFacultadRepository extends JpaRepository<Facultad, Long> {
}
