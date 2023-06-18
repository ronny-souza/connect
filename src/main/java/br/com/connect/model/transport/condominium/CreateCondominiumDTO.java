package br.com.connect.model.transport.condominium;

import br.com.connect.model.transport.address.AddressDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCondominiumDTO(@NotBlank String name, String phone, String email, @NotNull AddressDTO address) {
}
