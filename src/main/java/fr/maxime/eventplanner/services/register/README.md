
# Registration

Déroulé :

1- L'utilisateur envoie les données depuis le formulaire React  
2- L'authenticationController réceptionne les données et les envoie au RegisterService
3- Le RegisterService  :

	 1. controle les données
	 2. crée un token de confirmation
	 3. crée un email et l'envoi pour que l'utilisateur confirme son inscription
	 4. réceptionne l'email qui contient le token de confirmation et active le compte de l'utilisateur

## Envoie des données depuis React

Ce fait simplement au moyen d'un formulaire et d'une requête POST gérer via Axios.
Les données sont envoyées à http://localhost:8080/authentication/register

## Réception des données

Les données sont réceptionnées et insérées dans un objet `RegistrationRequest`. Cet objet est
ensuite envoyé au RegisterService.

## Le RegisterService

### Le contrôle des données
```java
// depuis la méthode registerUser()

// On check via une regex que l'email est bien valide structurellement
if (!emailValidator.test(request.getEmail())) {  
    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  
}  
  
// On check que l'email n'est pas déjà attribué
AppUser checkEmailAlreadyTaken = service.getByEmail(request.getEmail());  
if (checkEmailAlreadyTaken != null) {  
    throw new IllegalStateException(EMAIL_ALREADY_TAKEN);  
}


@Service  
public class EmailValidator implements Predicate<String> {  
    @Override  
  public boolean test(String s) {  
        String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";  
  
 boolean matcher = Pattern.matches(emailRegex, s);  
 return matcher;  
  }  
}


// Dans AppUserService on s'assure aussi que le username soit unique car il nous sert à l'authentication via JWT
// Donc dans la méthode create()
AppUser user = repository.findAppUserByUsername(item.getUsername())  
        .orElse(null);  
if (user != null) {  
    throw new IllegalStateException(USERNAME_ALREADY_TAKEN);  
}
```

### Création du token de confirmation

On va créer un token de confirmation qui sera envoyé dans l'email à l'utilisateur afin de s'assurer de pouvoir retrouver l'utilisateur et confirmer l'activation du compte de manière assez sécurisée

```java
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


// Le token est créé dans le ConfirmationTokenService puis sauver en BDD
public ConfirmationToken createConfirmationToken(AppUser savedItem) {  
    String token = UUID.randomUUID().toString();  
 return new ConfirmationToken(  
            token,  // le token
  LocalDateTime.now().plusDays(1),  // la date d'expiration du token
  LocalDateTime.now(),  // la date de création
  savedItem  // le AppUser associé à ce token
    );  
}


// dans la méthode create de AppUserService
ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(savedItem);  
ConfirmationToken save = confirmationTokenService.save(confirmationToken);
```

### Créer l'email et envoi

On crée l'email avec la librairy JavaMailSender
```java

// l'interface qu'on utilisera pour les injections dans les différents services
public interface MailSender {  
  
    void send(String to, String email);  
  String buildEmail(String name, String link);  
}


// l'implémentation de l'interface
@Service  
@AllArgsConstructor  
public class MailService implements MailSender {  
  
 private final static Logger LOG = LoggerFactory.getLogger(MailService.class);  
 private final JavaMailSender javaMailSender;  
  
  @Override  
  public void send(String to, String email) {  
        try {  
	          MimeMessage mimeMessage = javaMailSender.createMimeMessage();  
			  MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");  
			  helper.setText(email, true); // le contenu, la mention true précise qu'on veut un rendu html 
			  helper.setTo(to);  
			  helper.setFrom("***.*****@****.com");  
			  helper.setSubject("Confirm your email");  
			  javaMailSender.send(mimeMessage);  
  } catch (MessagingException e) {  
            LOG.error("Impossible d'envoyer l'email", e);  
			throw new IllegalStateException("Impossible d'envoyer l'email");  
  }  
  
    }  
  
    public String buildEmail(String name, String link) {  
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +  
                "\n" +  
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +  
                "\n" +  
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +  
                "    <tbody><tr>\n" +  
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +  
                " \n" +  
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +  
                "          <tbody><tr>\n" +  
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +  
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +  
                "                  <tbody><tr>\n" +  
                "                    <td style=\"padding-left:10px\">\n" +  
                " \n" +  
                "                    </td>\n" +  
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +  
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +  
                "                    </td>\n" +  
                "                  </tr>\n" +  
                "                </tbody></table>\n" +  
                "              </a>\n" +  
                "            </td>\n" +  
                "          </tr>\n" +  
                "        </tbody></table>\n" +  
                " \n" +  
                "      </td>\n" +  
                "    </tr>\n" +  
                "  </tbody></table>\n" +  
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +  
                "    <tbody><tr>\n" +  
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +  
                "      <td>\n" +  
                " \n" +  
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +  
                "                  <tbody><tr>\n" +  
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +  
                "                  </tr>\n" +  
                "                </tbody></table>\n" +  
                " \n" +  
                "      </td>\n" +  
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +  
                "    </tr>\n" +  
                "  </tbody></table>\n" +  
                "\n" +  
                "\n" +  
                "\n" +  
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +  
                "    <tbody><tr>\n" +  
                "      <td height=\"30\"><br></td>\n" +  
                "    </tr>\n" +  
                "    <tr>\n" +  
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +  
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +  
                " \n" +  
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +  
                " \n" +  
                "      </td>\n" +  
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +  
                "    </tr>\n" +  
                "    <tr>\n" +  
                "      <td height=\"30\"><br></td>\n" +  
                "    </tr>\n" +  
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +  
                "\n" +  
                "</div></div>";  
  }


// Dans AppUserService méthode create(), on crée le lien cliquable pour l'utilisateur dans l'email et on envoie
String link = "http://localhost:8080/authentication/confirm?token=" + save.getToken();  
mailSender.send(savedItem.getEmail(), mailSender.buildEmail(savedItem.getUsername(), link));

```

L'envoi des mails est configuré dans le application.properties avec mailtrap (https://help.mailtrap.io/article/12-getting-started-guide#send-to-Mailtrap)
### Confirmation suite au clique sur le lien

le clique appelle la méthode `confirm()` dans l'AuthenticationController à l'adresse suivante :
http://localhost:8080/authentication/confirm
Cette méthode `confirm()` appelle la méthode `enableAccount()` dans le RegistrationService

```java
@Transactional  
public ResponseEntity<AppUser> enableAccount(String token) throws IllegalStateException{  
	// on retrouve l'objet ConfirmationToken via le token
    ConfirmationToken byToken = confirmationTokenService.getByToken(token);
    // On retrouve le user associé  
  AppUser user = service.getById(byToken.getAppUser().getAppUserId());  
  
  // on s'assure que le token n'est pas déjà confirmé puis qu'il n'est pas expiré
 if (byToken.getConfirmedAt() != null) {  
        throw new IllegalStateException(TOKEN_ALREADY_CONFIRMED);  
  }  
    boolean checkExpirationToken = confirmationTokenService.checkExpirationToken(byToken);  
 if (checkExpirationToken) {  
        throw new IllegalStateException(TOKEN_EXPIRED);  
  }  
  
  // on confirme le token et on active le compte utilisateur puis on sauve sa en BDD
    byToken.setConfirmedAt(LocalDateTime.now());  
  confirmationTokenService.update(byToken.getConfirmationTokenId(), byToken);  
  user.setEnabled(true);  
  service.update(user.getAppUserId(), user);  
 return new ResponseEntity<>(user, HttpStatus.OK);  
}
```