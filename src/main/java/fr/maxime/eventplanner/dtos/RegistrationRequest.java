package fr.maxime.eventplanner.dtos;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RegistrationRequest {
    private final String username;
    private final String email;
    private final String password;
}
