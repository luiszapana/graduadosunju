package com.unju.graduados.repositories;

import com.unju.graduados.model.Universidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUniversidadRepository extends JpaRepository<Universidad, Long> {
}
