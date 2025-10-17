package com.unju.graduados.repositories;

import com.unju.graduados.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAnuncianteRepository extends JpaRepository<Usuario, Long> {
}
