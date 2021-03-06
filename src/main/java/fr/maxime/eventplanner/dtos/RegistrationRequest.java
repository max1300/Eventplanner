package fr.maxime.eventplanner.dtos;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
}
