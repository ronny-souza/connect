package br.com.connect.repository;

import br.com.connect.model.IdentityConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdentityConfirmationRepository extends JpaRepository<IdentityConfirmation, Long> {

    Optional<IdentityConfirmation> findByCodeAndUserEmail(String code, String email);

    Optional<IdentityConfirmation> findByCodeAndUserEmailOrEmail(String code, String userEmail, String email);

    Optional<IdentityConfirmation> findByCodeAndEmail(String code, String email);

    Optional<IdentityConfirmation> findByUserEmail(String email);

    Optional<IdentityConfirmation> findByEmailOrUserEmail(String email, String userEmail);
}
