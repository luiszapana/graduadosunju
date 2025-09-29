package com.unju.graduados.model; // Ajusta el paquete si es necesario

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Embeddable // Indica que esta clase es incrustable en otra entidad
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLoginPerfilesId implements Serializable {

    // Los nombres de los atributos deben coincidir con los de la entidad UsuarioLogin y Perfil
    private Long loginId; // Mapea a login_id
    private Long perfilesId; // Mapea a perfiles_id

    // Nota: Aunque JPA puede manejar esto, es buena práctica implementar
    // equals() y hashCode() para claves compuestas si no usas Lombok de forma estricta.
    // Lombok con @Getter/@Setter y @AllArgsConstructor ya genera una versión segura para uso simple.
}