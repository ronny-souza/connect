package br.com.connect.repository;

import br.com.connect.model.Condominium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CondominiumRepository extends JpaRepository<Condominium, Long> {

    Optional<Condominium> findByEmail(String email);

    Optional<Condominium> findByEmailAndUserConnectIdentifier(String email, String connectIdentifier);
}
