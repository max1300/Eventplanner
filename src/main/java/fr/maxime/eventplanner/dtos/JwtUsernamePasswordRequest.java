package fr.maxime.eventplanner.dtos;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class JwtUsernamePasswordRequest {

    private final String username;
    private final String password;

}
