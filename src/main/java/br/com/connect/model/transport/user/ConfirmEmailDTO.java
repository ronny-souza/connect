package br.com.connect.model.transport.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailDTO(@Email String email, @NotBlank String code) {
}
