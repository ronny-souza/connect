package br.com.connect.service;

import br.com.connect.exception.UserNotFoundException;
import br.com.connect.model.Condominium;
import br.com.connect.model.User;
import br.com.connect.model.enums.MailTypeEnum;
import br.com.connect.model.transport.condominium.CondominiumDTO;
import br.com.connect.model.transport.condominium.CreateCondominiumDTO;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.repository.CondominiumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CondominiumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CondominiumService.class);

    private final CondominiumRepository condominiumRepository;

    private final UserService userService;
    private final AccountConfirmationService accountConfirmationService;
    private final MailService mailService;

    public CondominiumService(CondominiumRepository condominiumRepository, UserService userService, AccountConfirmationService accountConfirmationService, MailService mailService) {
        this.condominiumRepository = condominiumRepository;
        this.userService = userService;
        this.accountConfirmationService = accountConfirmationService;
        this.mailService = mailService;
    }

    @Transactional
    public CondominiumDTO create(CreateCondominiumDTO createCondominiumDTO, UserDTO userInSession) throws UserNotFoundException {
        LOGGER.info("Starting condominium registration...");

        User user = this.userService.findByEmail(userInSession.email());
        Condominium condominium = new Condominium(createCondominiumDTO, user);
        this.condominiumRepository.save(condominium);

        if (createCondominiumDTO.email() != null) {
            this.publishConfirmationEmailMessage(createCondominiumDTO.email(), user);
        }

        return new CondominiumDTO(condominium);
    }

    private void publishConfirmationEmailMessage(String email, User user) {
        String subject = "Connect - Confirmação de e-mail do condomínio";
        MailTypeEnum type = MailTypeEnum.CONFIRM_CONDOMINIUM_EMAIL;

        String code = this.accountConfirmationService.createConfirmationCode(email, user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("code", code);
        this.mailService.buildAndpublish(null, user.getEmail(), subject, type, properties);
    }
}
