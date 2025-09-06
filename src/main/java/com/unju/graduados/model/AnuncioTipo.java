package com.unju.graduados.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anuncio_tipo")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AnuncioTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo;
    private String descripcion;

    @OneToMany(mappedBy = "tipoAnuncio")
    @Builder.Default
    private List<Anuncio> anuncios = new ArrayList<>();
}
