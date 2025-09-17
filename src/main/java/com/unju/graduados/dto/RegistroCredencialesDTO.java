package com.unju.graduados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroCredencialesDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirmPassword;

    /**
     * Verifica si password y confirmarPassword coinciden.
     */
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
