package com.unju.graduados.model;

import lombok.*;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // <--- CLAVE
    @Column(name = "fecha_colacion")
    private LocalDate fechaColacion;

    @Column(name = "anio_colacion")
    private Long anioColacion;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "usuario_creacion")
    private Long usuarioCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
}
