package br.com.connect.model.transport.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(@NotBlank String name, @Email @NotBlank String email, @NotBlank String phone,
                            @NotBlank String password) {
}
