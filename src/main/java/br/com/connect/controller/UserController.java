package br.com.connect.controller;

import br.com.connect.model.transport.CreateUserDTO;
import br.com.connect.model.transport.UserDTO;
import br.com.connect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> register(@RequestBody @Valid CreateUserDTO createUserDTO) {
        UserDTO response = this.userService.register(createUserDTO);
        return ResponseEntity.created(URI.create("/user")).build();
    }
}
