package com.unju.graduados.model;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "colacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Colacion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "colacion_seq")
    @SequenceGenerator(name = "colacion_seq", sequenceName = "colacion_seq", allocationSize = 1)
    private Long id;

    @OneToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "id_universidad", referencedColumnName = "id")
    private Universidad universidad;

    @OneToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "id_facultad", referencedColumnName = "id")
    private Facultad facultad;

    // Aunque la clase original usa una relación OneToOne, he ajustado el nombre de la columna a 'nro_orden'
    // que es un atributo en la tabla Colacion. Sin embargo, si la relación es a otra tabla
    // deberías verificar el nombre de la tabla relacionada (ColacionOrden).
    @OneToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "nro_orden", referencedColumnName = "id")
    private ColacionOrden orden;

    private String descripcion;

    @Column(name = "fecha_colacion")
    private Timestamp fechaColacion;

    @Column(name = "anio_colacion")
    private Long anioColacion;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @Column(name = "usuario_creacion")
    private Long usuarioCreacion;

    // No he incluido el método `getLabelColacion()` ni `toString()` generados en la versión original,
    // ya que Lombok puede generarlos automáticamente de forma predeterminada o se pueden
    // añadir con las anotaciones @ToString, etc. si lo necesitas.
}
