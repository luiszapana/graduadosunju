package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.Universidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUniversidadDao extends JpaRepository<Universidad, Long> {
}
