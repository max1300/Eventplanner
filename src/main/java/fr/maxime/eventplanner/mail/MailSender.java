package fr.maxime.eventplanner.mail;

public interface MailSender {

    void send(String to, String email);
    String buildEmail(String name, String link);
}
