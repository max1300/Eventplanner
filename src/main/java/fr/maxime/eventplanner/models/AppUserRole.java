package fr.maxime.eventplanner.models;

public enum AppUserRole {
    USER(Authorities.USER_AUTHORITY),
    ADMIN(Authorities.ADMIN_AUTORITY);


    private String[] authorities;

    AppUserRole(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
