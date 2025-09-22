package com.unju.graduados.model;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "colacion_orden")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColacionOrden implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name = "nro_orden")
    private Long nroOrden;

    private String titulo;
}