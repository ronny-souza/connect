package br.com.connect.model;

import br.com.connect.model.transport.condominium.CreateCondominiumDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Condominium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "connectIdentifier", nullable = false, unique = true)
    private String connectIdentifier;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private boolean emailActivated;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "condominium", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Tenant> tenants = new ArrayList<>();

    public Condominium(CreateCondominiumDTO createCondominiumDTO, User user) {
        this.connectIdentifier = UUID.randomUUID().toString();
        this.name = createCondominiumDTO.name();
        this.email = createCondominiumDTO.email();
        this.emailActivated = createCondominiumDTO.email() == null;
        this.phone = createCondominiumDTO.phone() != null ? createCondominiumDTO.phone() : user.getPhone();
        this.address = new Address(createCondominiumDTO.address());
        this.user = user;
    }

    public void activateEmail() {
        this.emailActivated = true;
    }
}
