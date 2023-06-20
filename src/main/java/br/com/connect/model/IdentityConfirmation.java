package br.com.connect.model;

import br.com.connect.factory.RandomFactory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "identityConfirmation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String connectIdentifier;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public IdentityConfirmation(String email, User user) {
        this.code = RandomFactory.instance().code();
        this.email = email;
        this.connectIdentifier = UUID.randomUUID().toString();
        this.expirationDate = LocalDateTime.now().plusHours(24);
        this.user = user;
    }
}
