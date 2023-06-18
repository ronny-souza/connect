package br.com.connect.model;

import br.com.connect.model.transport.user.CreateUserDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "connectIdentifier", nullable = false, unique = true)
    private String connectIdentifier;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT 0")
    private boolean enabled;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User(CreateUserDTO createUserDTO, String passwordEncoded) {
        this.connectIdentifier = UUID.randomUUID().toString();
        this.email = createUserDTO.email();
        this.name = createUserDTO.name();
        this.phone = createUserDTO.phone();
        this.password = passwordEncoded;
        this.createdAt = LocalDateTime.now();
    }

    public void enable() {
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
