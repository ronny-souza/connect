package br.com.connect.service;

import br.com.connect.exception.ConfirmationCodeExpiredException;
import br.com.connect.exception.ConfirmationCodeNotFoundException;
import br.com.connect.model.IdentityConfirmation;
import br.com.connect.model.User;
import br.com.connect.model.transport.user.ConfirmEmailDTO;
import br.com.connect.repository.IdentityConfirmationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IdentityConfirmationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityConfirmationService.class);

    private final IdentityConfirmationRepository identityConfirmationRepository;

    public IdentityConfirmationService(IdentityConfirmationRepository identityConfirmationRepository) {
        this.identityConfirmationRepository = identityConfirmationRepository;
    }

    @Transactional
    public String createConfirmationCode(String email, User user) {
        IdentityConfirmation identityConfirmation = new IdentityConfirmation(email, user);
        this.identityConfirmationRepository.save(identityConfirmation);
        return identityConfirmation.getCode();
    }

    @Transactional
    public void confirmAccount(ConfirmEmailDTO confirmEmailDTO) throws ConfirmationCodeExpiredException {
        LOGGER.info("Validating confirmation code...");
        Optional<IdentityConfirmation> optionalAccountConfirmation = this.identityConfirmationRepository.findByCodeAndUserEmail(confirmEmailDTO.code(), confirmEmailDTO.email());
        if (optionalAccountConfirmation.isEmpty()) {
            throw new ConfirmationCodeNotFoundException("Unable to confirm user account because code is not found");
        }

        IdentityConfirmation identityConfirmation = optionalAccountConfirmation.get();
        if (identityConfirmation.getExpirationDate().isBefore(LocalDateTime.now())) {
            this.identityConfirmationRepository.delete(identityConfirmation);
            throw new ConfirmationCodeExpiredException("The confirmation code has expired. Request a new code to try again");
        }

        this.identityConfirmationRepository.delete(identityConfirmation);
    }

    @Transactional
    public void confirmEmail(ConfirmEmailDTO confirmEmailDTO) throws ConfirmationCodeExpiredException {
        LOGGER.info("Validating confirmation code...");
        Optional<IdentityConfirmation> optionalAccountConfirmation = this.identityConfirmationRepository.findByCodeAndUserEmailOrEmail(confirmEmailDTO.code(), confirmEmailDTO.email(), confirmEmailDTO.email());
        if (optionalAccountConfirmation.isEmpty()) {
            throw new ConfirmationCodeNotFoundException("Unable to confirm email because code is not found");
        }

        IdentityConfirmation identityConfirmation = optionalAccountConfirmation.get();
        if (identityConfirmation.getExpirationDate().isBefore(LocalDateTime.now())) {
            this.identityConfirmationRepository.delete(identityConfirmation);
            throw new ConfirmationCodeExpiredException("The confirmation code has expired. Request a new code to try again");
        }

        this.identityConfirmationRepository.delete(identityConfirmation);
    }

    @Transactional
    public void deleteCodeIfExists(String email) {
        Optional<IdentityConfirmation> optionalAccountConfirmation = this.identityConfirmationRepository.findByEmailOrUserEmail(email, email);
        optionalAccountConfirmation.ifPresent(this.identityConfirmationRepository::delete);
    }
}
