package com.unju.graduados.repositories;

import com.unju.graduados.model.ColacionOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IColacionOrdenRepository extends JpaRepository<ColacionOrden, Long> { }

