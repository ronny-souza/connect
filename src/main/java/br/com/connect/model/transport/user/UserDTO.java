package br.com.connect.model.transport.user;

import br.com.connect.model.User;

public record UserDTO(String email, String name, String phone) {

    public UserDTO(User user) {
        this(user.getEmail(), user.getName(), user.getPhone());
    }
}
