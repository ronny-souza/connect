package br.com.connect.model.transport.user;

import br.com.connect.model.User;

public record UserDTO(String connectIdentifier, String email, String name, String phone) {

    public UserDTO(User user) {
        this(user.getConnectIdentifier(), user.getEmail(), user.getName(), user.getPhone());
    }
}
