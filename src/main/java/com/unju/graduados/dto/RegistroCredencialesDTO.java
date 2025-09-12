package com.unju.graduados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegistroCredencialesDTO {
    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "La contraseña debe tener mínimo 8 caracteres, al menos 1 mayúscula, 1 minúscula y 1 número"
    )
    private String password;

    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirmPassword;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}