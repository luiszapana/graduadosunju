package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.AnuncioTipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoAnuncioDao extends JpaRepository<AnuncioTipo, Long> {
}
