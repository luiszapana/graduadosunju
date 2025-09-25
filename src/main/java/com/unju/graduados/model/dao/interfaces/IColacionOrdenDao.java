package com.unju.graduados.model.dao.interfaces;

import com.unju.graduados.model.ColacionOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IColacionOrdenDao extends JpaRepository<ColacionOrden, Long> { }

