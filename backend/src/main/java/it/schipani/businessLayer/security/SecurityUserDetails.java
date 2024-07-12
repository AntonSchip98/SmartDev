package it.schipani.businessLayer.security;

import it.schipani.dataLayer.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

@Data
@AllArgsConstructor
@Builder(setterPrefix = "with")
/* implementa l'interfaccia UserDetails di Spring Security, utilizzata per rappresentare le
informazioni di autenticazione e autorizzazione di un utente. La classe è annotata con Lombok
 per ridurre il boilerplate code.*/
public class SecurityUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private String username;
    @Builder.Default
    private boolean accountNonExpired = true;
    @Builder.Default
    private boolean accountNonLocked = true;
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @Builder.Default
    private boolean enabled = true;

    public static SecurityUserDetails build(User user) {
        return SecurityUserDetails.builder()
                .withUsername(user.getUsername())
                .withPassword(user.getPassword())
                .build();
    }
}