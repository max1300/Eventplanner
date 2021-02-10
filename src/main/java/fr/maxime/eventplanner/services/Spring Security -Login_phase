## **Login via le JWT**

*Déroulé d'une authentification utilisateur :*

1. Saisi des infos utilisateur sur le site avec le formulaire de login
1. Back qui recoit les infos et tente d'authentifier l'utilisateur
1. Création et enregistrement d'un token JWT
2. reponse retour avec le JWT et le username et role de l'utilisateur


#### 1- Saisi des infos utilisateurs
Ceci ce fait simplement avec React JS. Un formulaire est disponible qui permet de rentrer ces infos. Celles-ci sont collectées puis envoyées grâce à une requete Axios vers le endpoint http://localhost:8080/authentication/login


#### 2- Authentification de l'utilisateur

``` java
@Service
@AllArgsConstructor
public class LoginService {

	// AuthenticationManager qui va réaliser l'authentication en tachant de
	// selectionner le bon provider (ici ce sera UsernameAndPasswordAuthenticationToken)
    private final AuthenticationManager authenticationManager;
    
    // La classe qui contient les méthodes de génération du token
    private final JwtProvider jwtProvider;

    public ResponseEntity<AuthenticationResponse> authenticationAttempt(JwtUsernamePasswordRequest request) {

		// On crée donc un UsernameAndPasswordAuthenticationToken qui prend en
		// argument de constuctor un UserPrincipal (voir l'implémentation de
		// UserDetails ci dessus)
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		// On crée le token
        String jwtProviderToken = jwtProvider.createToken(authenticate);

		// On crée une réponse custom pour renvoyer tous les éléments essentiels 
		// dans la réponse au client
        AuthenticationResponse response = getAuthenticationResponse(authenticate, jwtProviderToken);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    private AuthenticationResponse getAuthenticationResponse(Authentication authentication, String jwtToken) {
        AppUser principal = (AppUser) authentication.getPrincipal();
        return new AuthenticationResponse(jwtToken, principal.getUsername(), principal.getAppUserRole().name());
    }
```


```java

// Il s'agit de la classe User par défaut, cependant elle impléments *UserDetails* // qui est une interface qui sert à encapsuler les informations afin de demander
// ensuite une authentication
public class AppUser implements UserDetails {

    @SequenceGenerator(name = "appUser_sequence", sequenceName = "appUser_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appUser_sequence")
    private Long appUserId;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private Boolean locked = false;
    private Boolean enabled = false;

    public AppUser(String username, String email, String password, AppUserRole appUserRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.appUserRole = appUserRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
```

```java
// Dans la classe AppUserService nous avons implémenté cette méthode.
// Elle va nous servir de continuité au UserDetails puisque elle permet d'interroger
// la BDD afin de trouver un user correspondant et de renvoyer un UserDetails  
// encapsulant les données du User nécessaires à une authentication
@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findAppUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username with username %s not found", username)));
    }
```

Ensuite l'AuthenticationManager prend le relais et réalise l'authenification qui soit réussi et alors on passe à la création du JwtToken (voir 3A) soit fail et donc on reçoit un message d'erreur (voir 3B)

#### 3A- Le Jwt token

```java
// Class JwtProvider
// On utilise la library JJwt pour créer le JWT

public String createToken(Authentication authentication) {
        return Jwts.builder() // On lancer le Builder()
                .setSubject(authentication.getName()) // le username du user
                .claim("authorities", authentication.getAuthorities()) // ses permissions et son role
                .setIssuedAt(new Date()) // la date d'émission
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2))) // la date d'expiration
                .signWith(secretKey) // la clé secrete qui permettra plus tard de verifier la validité du token
                .compact();
    }

```

#### 3B- L'authentication fail

```java
// Classe qui surcharge les erreurs survenues lors des tentatives de login

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    // Custom class pour surchargé le comportement d'une erreur lors d'une authentification insuffisante
    // On fait ça afin de rendre plus clair les refus d'authentication

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException {
    
    		// On crée nos propres response custom pour renvoyer qulque chose de plus clair
        CustomHttpResponse httpResponse = new CustomHttpResponse(
                FORBIDDEN.value(),
                FORBIDDEN,
                FORBIDDEN.getReasonPhrase().toUpperCase(Locale.ROOT),
                "Vous devez être loggé pour accéder à ce contenu"
        );

		// On précise bien que la réponse renvoie du JSON
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        
        // En gros sur les prochaines lignes on va utiliser l'objectMapper afin d'insérer notre custom response dans la response qui sera renvoyée
        OutputStream stream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(stream, httpResponse);
        stream.flush();
    }
```

#### 4- La réponse

```java
// Classe qui construit la response suite à une authentication réussie
// elle renvoie le token, le username et le role

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AuthenticationResponse {

    private final String token;
    private final String username;
    private final String role;
}
```
