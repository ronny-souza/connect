package br.com.connect.service;

import br.com.connect.exception.ConfirmationCodeExpiredException;
import br.com.connect.exception.ConfirmationCodeNotFoundException;
import br.com.connect.model.AccountConfirmation;
import br.com.connect.model.User;
import br.com.connect.model.transport.user.ConfirmAccountDTO;
import br.com.connect.repository.AccountConfirmationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountConfirmationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountConfirmationService.class);

    private final AccountConfirmationRepository accountConfirmationRepository;

    public AccountConfirmationService(AccountConfirmationRepository accountConfirmationRepository) {
        this.accountConfirmationRepository = accountConfirmationRepository;
    }

    @Transactional
    public String createConfirmationCode(String email, User user) {
        AccountConfirmation accountConfirmation = new AccountConfirmation(email, user);
        this.accountConfirmationRepository.save(accountConfirmation);
        return accountConfirmation.getCode();
    }

    @Transactional
    public void confirmAccount(ConfirmAccountDTO confirmAccountDTO) throws ConfirmationCodeExpiredException {
        LOGGER.info("Validating confirmation code...");
        Optional<AccountConfirmation> optionalAccountConfirmation = this.accountConfirmationRepository.findByCodeAndUserEmail(confirmAccountDTO.code(), confirmAccountDTO.email());
        if (optionalAccountConfirmation.isEmpty()) {
            throw new ConfirmationCodeNotFoundException("Unable to confirm user account because code is not found");
        }

        AccountConfirmation accountConfirmation = optionalAccountConfirmation.get();
        if (accountConfirmation.getExpirationDate().isBefore(LocalDateTime.now())) {
            this.accountConfirmationRepository.delete(accountConfirmation);
            throw new ConfirmationCodeExpiredException("The confirmation code has expired. Request a new code to try again");
        }

        this.accountConfirmationRepository.delete(accountConfirmation);
    }

    @Transactional
    public void deleteCodeIfExists(String email) {
        Optional<AccountConfirmation> optionalAccountConfirmation = this.accountConfirmationRepository.findByUserEmail(email);
        optionalAccountConfirmation.ifPresent(this.accountConfirmationRepository::delete);
    }
}
