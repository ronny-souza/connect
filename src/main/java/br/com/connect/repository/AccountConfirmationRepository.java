package br.com.connect.repository;

import br.com.connect.model.AccountConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountConfirmationRepository extends JpaRepository<AccountConfirmation, Long> {

    Optional<AccountConfirmation> findByCodeAndUserEmail(String code, String email);

    Optional<AccountConfirmation> findByUserEmail(String email);
}
