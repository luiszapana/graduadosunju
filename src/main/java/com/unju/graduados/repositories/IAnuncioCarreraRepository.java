package com.unju.graduados.repositories;

import com.unju.graduados.model.AnuncioCarrera;
import com.unju.graduados.model.AnuncioCarreraPK; // Necesitas esta clase
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAnuncioCarreraRepository extends JpaRepository<AnuncioCarrera, AnuncioCarreraPK> {

}
