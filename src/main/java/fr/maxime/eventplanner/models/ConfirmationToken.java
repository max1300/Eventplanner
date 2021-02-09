package fr.maxime.eventplanner.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "CONFIRMATION_TOKEN")
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "confirmationToken_sequence")
    @SequenceGenerator(name = "confirmationToken_sequence", sequenceName = "confirmationToken_sequence", allocationSize = 1)
    @Column(name = "CONFIRMATION_TOKEN_ID", nullable = false)
    private Long confirmationTokenId;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "CONFIRMED_AT")
    private LocalDateTime confirmedAt;

    @Column(name = "EXPIRED_AT", nullable = false)
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "APP_USER_APPUSERID")
    private AppUser appUser;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;


    public ConfirmationToken(String token, LocalDateTime expiredAt,
                             LocalDateTime createdAt, AppUser user) {
        this.token = token;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
        this.appUser = user;
    }
}