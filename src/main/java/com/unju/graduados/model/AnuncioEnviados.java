package com.unju.graduados.model; // Ajusta el paquete si es necesario

import jakarta.persistence.*; // Importaciones de Jakarta
import lombok.AllArgsConstructor;
import lombok.Builder; // Útil para crear objetos de manera limpia
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp; // Usando Timestamp, puedes considerar java.time.LocalDateTime

@Entity
@Table(name="anuncio_enviados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // <--- Opcional pero recomendado para crear instancias de manera legible
public class AnuncioEnviados {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="anuncio_enviados_seq")
    @SequenceGenerator(name="anuncio_enviados_seq", sequenceName="anuncio_enviados_seq", allocationSize=1)
    private Long id;

    @Column(name="id_anuncio")
    private Long idAnuncio;

    @Column(name="id_usuario_datos_academicos")
    private Long idUsuarioDatosAcademicos;

    @Column(name="fecha_envio")
    private Timestamp fechaEnvio;

    @Column(name="estado")
    private String estado;
}