package br.com.connect.service;

import br.com.connect.exception.UnauthenticatedUserException;
import br.com.connect.model.User;
import br.com.connect.model.transport.user.UserDTO;
import br.com.connect.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

    private final UserRepository userRepository;

    public UserSessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getUserInSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();

            User user = this.userRepository.findByEmail(email);

            if (user != null) {
                return new UserDTO(user);
            }

            throw new UnauthenticatedUserException(String.format("No user found with the following email: %s", email));
        }

        throw new UnauthenticatedUserException("Only authenticated users can perform this operation");
    }
}
