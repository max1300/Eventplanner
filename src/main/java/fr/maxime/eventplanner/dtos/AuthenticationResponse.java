package fr.maxime.eventplanner.dtos;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AuthenticationResponse {

    private final String token;
    private final String username;
    private final String role;
}
