package br.com.connect.service;

import br.com.connect.exception.UserAlreadyExistsException;
import br.com.connect.factory.RandomFactory;
import br.com.connect.model.User;
import br.com.connect.model.enums.MailTypeEnum;
import br.com.connect.model.transport.CreateUserDTO;
import br.com.connect.model.transport.UserDTO;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email);
    }

    @Transactional
    public UserDTO register(CreateUserDTO createUserDTO) {
        LOGGER.info("Starting user registration...");
        if (this.userRepository.existsByEmail(createUserDTO.email())) {
            throw new UserAlreadyExistsException("User with given email already exists");
        }

        String passwordEncoded = this.passwordEncoder.encode(createUserDTO.password());
        User newUser = new User(createUserDTO, passwordEncoded);
        this.userRepository.save(newUser);
        LOGGER.info("User registered, sending confirmation account email...");

        UserDTO userDTO = new UserDTO(newUser);
        this.publishConfirmationAccountMessage(userDTO);
        return userDTO;
    }

    private void publishConfirmationAccountMessage(UserDTO userDTO) {
        String subject = "Connect - Confirmação de conta";
        MailTypeEnum type = MailTypeEnum.CONFIRM_ACCOUNT;

        String code = RandomFactory.instance().code();

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", userDTO.name());
        properties.put("code", code);
        this.mailService.buildAndpublish(null, userDTO.email(), subject, type, properties);
    }
}
