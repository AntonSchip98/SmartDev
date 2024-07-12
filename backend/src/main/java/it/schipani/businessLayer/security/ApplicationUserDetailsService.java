package it.schipani.businessLayer.security;


import it.schipani.dataLayer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//Bean di Servizio di recupero di un utente tramite le procedure di gestione utente di Spring Security
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = users.findOneByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username));
        return SecurityUserDetails.build(user);
    }
}