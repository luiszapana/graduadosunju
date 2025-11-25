package com.unju.graduados.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Embeddable // Indica que esta clase es parte de la clave de otra entidad
@Data       // Getters, Setters, hashCode, equals (CRUCIAL para claves compuestas)
@NoArgsConstructor
@AllArgsConstructor
public class AnuncioCarreraPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "id_anuncio")
    private Long idAnuncio;

    @Column(name = "id_carrera")
    private Long idCarrera;
}