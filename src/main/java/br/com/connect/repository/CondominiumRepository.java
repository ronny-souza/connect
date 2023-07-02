package br.com.connect.repository;

import br.com.connect.configuration.projection.JpaSpecificationExecutorWithProjection;
import br.com.connect.model.Condominium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CondominiumRepository extends JpaRepository<Condominium, Long>, JpaSpecificationExecutorWithProjection<Condominium, Long> {

    Optional<Condominium> findByEmail(String email);

    Optional<Condominium> findByEmailAndUserConnectIdentifier(String email, String connectIdentifier);

    Optional<Condominium> findByConnectIdentifierAndUserConnectIdentifierAndUserEnabledTrue(String connectIdentifier, String userConnectIdentifier);

    boolean existsByEmail(String email);

    <T> Page<T> findBy(Specification<Condominium> specification, Pageable pageable, Class<T> projection);
}
