package it.schipani.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

    @Bean
    PasswordEncoder stdPasswordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    AuthTokenFilter authenticationJwtToken() {
        return new AuthTokenFilter();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Utilizza la configurazione CORS
                .authorizeHttpRequests(authorize -> authorize //Configurazione delle protezioni dei vari endpoint
                        .requestMatchers("/api/users/login").permitAll() // Permetti l'accesso anonimo all'API di login
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll() // Permetti la registrazione di un utente
                        .requestMatchers(HttpMethod.GET, "/**").authenticated() // Richiedi autenticazione per tutte le GET
                        .requestMatchers(HttpMethod.POST, "/**").authenticated() // Richiedi autenticazione per tutte le POST
                        .requestMatchers(HttpMethod.PUT, "/**").authenticated() // Richiedi autenticazione per tutte le PUT
                        .requestMatchers(HttpMethod.PATCH, "/**").authenticated() // Richiedi autenticazione per tutte le PATCH
                        .requestMatchers(HttpMethod.DELETE, "/**").authenticated() // Richiedi autenticazione per tutte le DELETE
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationJwtToken(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}