package br.com.connect.controller;

import br.com.connect.exception.CondominiumNotFoundException;
import br.com.connect.exception.ConfirmationCodeExpiredException;
import br.com.connect.exception.UserNotFoundException;
import br.com.connect.model.transport.condominium.CondominiumDTO;
import br.com.connect.model.transport.condominium.CreateCondominiumDTO;
import br.com.connect.model.transport.user.ConfirmEmailDTO;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.service.CondominiumService;
import br.com.connect.service.UserSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/condominium")
public class CodominiumController {

    private final CondominiumService condominiumService;

    private final UserSessionService userSessionService;

    public CodominiumController(CondominiumService condominiumService, UserSessionService userSessionService) {
        this.condominiumService = condominiumService;
        this.userSessionService = userSessionService;
    }

    @PostMapping
    public ResponseEntity<CondominiumDTO> create(@RequestBody @Valid CreateCondominiumDTO createCondominiumDTO) throws UserNotFoundException {
        UserDTO userInSession = this.userSessionService.getUserInSession();
        CondominiumDTO response = this.condominiumService.create(createCondominiumDTO, userInSession);
        return ResponseEntity.created(URI.create("/condominium")).body(response);
    }

    @PostMapping("/confirmation")
    public ResponseEntity<Void> confirmEmail(@RequestBody @Valid ConfirmEmailDTO confirmEmailDTO) throws ConfirmationCodeExpiredException, CondominiumNotFoundException {
        this.condominiumService.confirmEmail(confirmEmailDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirmation/resend")
    public ResponseEntity<Void> resendEmailConfirmationCode(@RequestBody String email) throws UserNotFoundException, CondominiumNotFoundException {
        UserDTO userInSession = this.userSessionService.getUserInSession();
        this.condominiumService.regenerateConfirmationCode(email, userInSession);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
