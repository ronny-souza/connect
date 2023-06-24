package br.com.connect.repository;

import br.com.connect.model.Condominium;
import br.com.connect.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<List<Tenant>> findByCondominium(Condominium condominium);
}
