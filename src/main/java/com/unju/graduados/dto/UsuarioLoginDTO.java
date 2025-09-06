package com.unju.graduados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioLoginDTO {
    //@Email
    @NotBlank
    private String usuario;

    @NotBlank
    @Size(min = 8)
    private String password;
}
