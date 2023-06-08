package br.com.connect.model;

import br.com.connect.factory.RandomFactory;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accountConfirmation")
public class AccountConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String connectIdentifier;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public AccountConfirmation() {
        
    }

    public AccountConfirmation(User user) {
        this.code = RandomFactory.instance().code();
        this.connectIdentifier = UUID.randomUUID().toString();
        this.expirationDate = LocalDateTime.now().plusHours(24);
        this.user = user;
    }

    public String getConnectIdentifier() {
        return connectIdentifier;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public User getUser() {
        return user;
    }
}
