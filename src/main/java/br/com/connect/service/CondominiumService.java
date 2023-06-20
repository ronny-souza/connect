package br.com.connect.service;

import br.com.connect.exception.CondominiumNotFoundException;
import br.com.connect.exception.ConfirmationCodeExpiredException;
import br.com.connect.exception.UserNotFoundException;
import br.com.connect.model.Condominium;
import br.com.connect.model.User;
import br.com.connect.model.enums.MailTypeEnum;
import br.com.connect.model.transport.condominium.CondominiumDTO;
import br.com.connect.model.transport.condominium.CreateCondominiumDTO;
import br.com.connect.model.transport.user.ConfirmEmailDTO;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.repository.CondominiumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CondominiumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CondominiumService.class);

    private final CondominiumRepository condominiumRepository;

    private final UserService userService;
    private final IdentityConfirmationService identityConfirmationService;
    private final MailService mailService;

    public CondominiumService(CondominiumRepository condominiumRepository, UserService userService, IdentityConfirmationService identityConfirmationService, MailService mailService) {
        this.condominiumRepository = condominiumRepository;
        this.userService = userService;
        this.identityConfirmationService = identityConfirmationService;
        this.mailService = mailService;
    }

    @Transactional
    public CondominiumDTO create(CreateCondominiumDTO createCondominiumDTO, UserDTO userInSession) throws UserNotFoundException {
        LOGGER.info("Starting condominium registration...");

        User user = this.userService.findByEmail(userInSession.email());
        Condominium condominium = new Condominium(createCondominiumDTO, user);
        this.condominiumRepository.save(condominium);

        if (createCondominiumDTO.email() != null) {
            this.publishConfirmationEmailMessage(createCondominiumDTO.name(), createCondominiumDTO.email(), user);
        }

        return new CondominiumDTO(condominium);
    }

    @Transactional
    public void confirmEmail(ConfirmEmailDTO confirmEmailDTO) throws ConfirmationCodeExpiredException, CondominiumNotFoundException {
        this.identityConfirmationService.confirmEmail(confirmEmailDTO);

        Optional<Condominium> optionalCondominium = this.condominiumRepository.findByEmail(confirmEmailDTO.email());
        if (optionalCondominium.isEmpty()) {
            throw new CondominiumNotFoundException(String.format("Condominium with email: %s is not found", confirmEmailDTO.email()));
        }
        Condominium condominium = optionalCondominium.get();
        condominium.activateEmail();
        this.condominiumRepository.save(condominium);
    }

    public void regenerateConfirmationCode(String email, UserDTO userInSession) throws CondominiumNotFoundException {
        Optional<Condominium> optionalCondominium = this.condominiumRepository.findByEmailAndUserConnectIdentifier(email, userInSession.connectIdentifier());
        if (optionalCondominium.isEmpty()) {
            throw new CondominiumNotFoundException("Condominium was not found or user is not the owner");
        }

        Condominium condominium = optionalCondominium.get();
        this.identityConfirmationService.deleteCodeIfExists(email);
        LOGGER.info("Generating new confirmation code...");
        this.publishConfirmationEmailMessage(condominium.getName(), email, condominium.getUser());
    }

    private void publishConfirmationEmailMessage(String condominiumName, String email, User user) {
        String subject = "Connect - Confirmação de e-mail do condomínio";
        MailTypeEnum type = MailTypeEnum.CONFIRM_CONDOMINIUM_EMAIL;

        String code = this.identityConfirmationService.createConfirmationCode(email, user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("code", code);
        properties.put("condominium", condominiumName);
        this.mailService.buildAndpublish(null, email, subject, type, properties);
    }
}
