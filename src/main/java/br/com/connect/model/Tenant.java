package br.com.connect.model;

import br.com.connect.model.transport.condominium.tenant.CreateTenantDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "email")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "connectIdentifier", nullable = false, unique = true)
    private String connectIdentifier;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer apartment;

    private String complement;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime leftAt;

    private LocalDateTime entryDate;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "condominium_id", nullable = false)
    private Condominium condominium;

    public Tenant(CreateTenantDTO createTenantDTO, Condominium condominium) {
        this.connectIdentifier = UUID.randomUUID().toString();
        this.name = createTenantDTO.getName();
        this.email = createTenantDTO.getEmail();
        this.apartment = createTenantDTO.getApartment();
        this.complement = createTenantDTO.getComplement() != null && !createTenantDTO.getComplement().isBlank() ? createTenantDTO.getComplement() : null;
        this.contact = createTenantDTO.getContact();
        this.createdAt = LocalDateTime.now();
        this.condominium = condominium;
    }
}
