package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "anuncio_carreras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnuncioCarrera implements Serializable {
    private static final long serialVersionUID = 1L;

    // 💡 Usamos @EmbeddedId para referenciar la clave compuesta
    @EmbeddedId
    private AnuncioCarreraPK id;

    // Si la tabla tuviera otras columnas (ej. fecha_creacion), irían aquí.

    // Constructor de conveniencia para la lógica del servicio
    public AnuncioCarrera(Long idAnuncio, Long idCarrera) {
        this.id = new AnuncioCarreraPK(idAnuncio, idCarrera);
    }
}
