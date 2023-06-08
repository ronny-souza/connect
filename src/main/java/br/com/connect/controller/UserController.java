package br.com.connect.controller;

import br.com.connect.model.transport.user.ConfirmAccountDTO;
import br.com.connect.model.transport.user.CreateUserDTO;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.service.AccountConfirmationService;
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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> register(@RequestBody @Valid CreateUserDTO createUserDTO) {
        UserDTO response = this.userService.register(createUserDTO);
        return ResponseEntity.created(URI.create("/user")).body(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmAccount(@RequestBody @Valid ConfirmAccountDTO confirmAccountDTO) {
        this.userService.confirmAccount(confirmAccountDTO);
        return ResponseEntity.ok().build();
    }
}
