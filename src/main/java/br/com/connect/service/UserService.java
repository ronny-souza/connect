package br.com.connect.service;

import br.com.connect.exception.ConfirmationCodeExpiredException;
import br.com.connect.exception.UserAlreadyExistsException;
import br.com.connect.exception.UserNotFoundException;
import br.com.connect.model.User;
import br.com.connect.model.enums.MailTypeEnum;
import br.com.connect.model.transport.user.ConfirmEmailDTO;
import br.com.connect.model.transport.user.CreateUserDTO;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    private final IdentityConfirmationService identityConfirmationService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService, IdentityConfirmationService identityConfirmationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.identityConfirmationService = identityConfirmationService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email);
    }

    public User findByEmail(String email) throws UserNotFoundException {
        return this.userRepository.findByEmailAndEnabledTrue(email).orElseThrow(() -> new UserNotFoundException(String.format("User with given e-mail was not found: %s", email)));
    }

    @Transactional
    public UserDTO register(CreateUserDTO createUserDTO) {
        LOGGER.info("Starting user registration...");
        if (this.userRepository.existsByEmail(createUserDTO.email())) {
            throw new UserAlreadyExistsException("User with given email already exists");
        }

        String passwordEncoded = this.passwordEncoder.encode(createUserDTO.password());
        User createdUser = this.userRepository.save(new User(createUserDTO, passwordEncoded));
        LOGGER.info("User registered, sending confirmation account email...");

        this.publishConfirmationAccountMessage(createdUser);
        return new UserDTO(createdUser);
    }

    public void regenerateConfirmationCode(String email) throws UserNotFoundException {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("No user registered with this email was found");
        }

        this.identityConfirmationService.deleteCodeIfExists(email);
        LOGGER.info("Generating new confirmation code...");
        this.publishConfirmationAccountMessage(user);
    }

    private void publishConfirmationAccountMessage(User user) {
        String subject = "Connect - Confirmação de conta";
        MailTypeEnum type = MailTypeEnum.CONFIRM_ACCOUNT;

        String code = this.identityConfirmationService.createConfirmationCode(null, user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", user.getName());
        properties.put("code", code);
        this.mailService.buildAndpublish(null, user.getEmail(), subject, type, properties);
    }

    @Transactional
    public void confirmAccount(ConfirmEmailDTO confirmEmailDTO) throws ConfirmationCodeExpiredException {
        this.identityConfirmationService.confirmAccount(confirmEmailDTO);

        User user = this.userRepository.findByEmail(confirmEmailDTO.email());
        user.enable();
        this.userRepository.save(user);
    }
}
