package com.unju.graduados.model.repositories;

import com.unju.graduados.model.AnuncioTipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoAnuncioRepository extends JpaRepository<AnuncioTipo, Long> {
}
