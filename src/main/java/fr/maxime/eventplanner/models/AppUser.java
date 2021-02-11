package fr.maxime.eventplanner.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {

    @SequenceGenerator(name = "appUser_sequence", sequenceName = "appUser_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appUser_sequence")
    private Long appUserId;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private boolean isActive;
    private boolean isNotLocked;

    public AppUser(String username, String email, String password, AppUserRole appUserRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.appUserRole = appUserRole;
    }


}
