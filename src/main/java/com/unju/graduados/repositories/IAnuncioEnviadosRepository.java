package com.unju.graduados.repositories;

import com.unju.graduados.model.AnuncioEnviados; // Asumo que tienes una entidad JPA llamada AnuncioEnviados
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAnuncioEnviadosRepository extends JpaRepository<AnuncioEnviados, Long> {
    // Spring Data JPA proporciona automáticamente métodos como save(), findById(), etc.
    // Puedes agregar aquí métodos de consulta personalizados si los necesitas,
    // por ejemplo, para verificar si un usuario ya recibió un anuncio:

    // Optional<AnuncioEnviados> findByIdAnuncioAndIdUsuario(Long idAnuncio, Long idUsuario);
}
