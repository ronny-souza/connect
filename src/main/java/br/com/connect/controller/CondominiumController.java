package br.com.connect.controller;

import br.com.connect.configuration.web.SearchParamsParserConfiguration;
import br.com.connect.exception.*;
import br.com.connect.model.transport.condominium.CondominiumDTO;
import br.com.connect.model.transport.condominium.CreateCondominiumDTO;
import br.com.connect.model.transport.condominium.projection.AvailableCondominiumDTO;
import br.com.connect.model.transport.search.ListSearchCriteriaDTO;
import br.com.connect.model.transport.user.ConfirmEmailDTO;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.service.CondominiumService;
import br.com.connect.service.UserSessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/condominium")
public class CondominiumController {

    private final CondominiumService condominiumService;

    private final UserSessionService userSessionService;

    private final SearchParamsParserConfiguration searchParamsParserConfiguration;

    public CondominiumController(CondominiumService condominiumService, UserSessionService userSessionService, SearchParamsParserConfiguration searchParamsParserConfiguration) {
        this.condominiumService = condominiumService;
        this.userSessionService = userSessionService;
        this.searchParamsParserConfiguration = searchParamsParserConfiguration;
    }

    @GetMapping("/availables")
    public ResponseEntity<Page<AvailableCondominiumDTO>> listAvailables(@PageableDefault(size = 10, sort = "name") Pageable pagination, @RequestParam(value = "search", required = false) ListSearchCriteriaDTO searchParams) {
        UserDTO userInSession = this.userSessionService.getUserInSession();

        Page<AvailableCondominiumDTO> response = this.condominiumService.listAvailableCondominiums(pagination, searchParams, userInSession);
        if (response.hasContent()) {
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CondominiumDTO> create(@RequestBody @Valid CreateCondominiumDTO createCondominiumDTO) throws UserNotFoundException, CondominiumEmailAlreadyRegisteredException {
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

    @PostMapping("/{condominiumUUID}/tenant")
    public ResponseEntity<Void> insertTenants(@PathVariable("condominiumUUID") final String condominiumUUID, @RequestParam("tenants") MultipartFile tenantsAsCsv) throws EmptyFileException, CondominiumNotFoundException, ImportTenantsException {
        UserDTO userInSession = this.userSessionService.getUserInSession();
        this.condominiumService.registerTenants(tenantsAsCsv, condominiumUUID, userInSession);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
