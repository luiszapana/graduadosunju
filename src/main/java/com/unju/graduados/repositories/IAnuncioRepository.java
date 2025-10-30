package com.unju.graduados.repositories;

import com.unju.graduados.model.Anuncio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull; // Importación necesaria para el fix
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAnuncioRepository extends JpaRepository<Anuncio, Long>, JpaSpecificationExecutor<Anuncio> {

    /**
     * Sobreescribe findAll para asegurar que los campos relacionados (tipoAnuncio, carreras)
     * se carguen eagerlmente, evitando problemas de N+1.
     * Se añaden las anotaciones @NonNull a los parámetros para satisfacer el contrato
     * de @NonNullApi del JpaRepository.
     */
    @EntityGraph(attributePaths = {"tipoAnuncio", "carreras"})
    @Override
    @NonNull // Se añade al valor de retorno
    Page<Anuncio> findAll(Specification<Anuncio> spec, @NonNull Pageable pageable);

    /**
     * Sobreescribe findById para asegurar la carga eager del tipoAnuncio y carreras.
     * Se añade la anotación @NonNull al parámetro ID y al valor de retorno.
     */
    @EntityGraph(attributePaths = {"tipoAnuncio", "carreras"})
    @Override
    @NonNull // Se añade al valor de retorno para satisfacer @NonNullApi
    Optional<Anuncio> findById(@NonNull Long id);
}