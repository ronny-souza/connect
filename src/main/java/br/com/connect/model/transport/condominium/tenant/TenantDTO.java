package br.com.connect.model.transport.condominium.tenant;

import br.com.connect.model.transport.user.UserDTO;

import java.time.LocalDateTime;

public record TenantDTO(String name, String email, Integer apartment, String complement, String contact,
                        LocalDateTime createdAt, LocalDateTime leftAt, LocalDateTime entryDate, UserDTO user) {
}
