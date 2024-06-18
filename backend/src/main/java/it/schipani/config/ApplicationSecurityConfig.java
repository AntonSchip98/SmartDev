package it.schipani.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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

import java.util.Properties;

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

   /* @Bean
    public JavaMailSenderImpl getJavaMailSender(@Value("${gmail.mail.transport.protocol}") String protocol,
                                                @Value("${gmail.mail.smtp.auth}") String auth,
                                                @Value("${gmail.mail.smtp.starttls.enable}") String starttls,
                                                @Value("${gmail.mail.debug}") String debug,
                                                @Value("${gmail.mail.from}") String from,
                                                @Value("${gmail.mail.from.password}") String password,
                                                @Value("${gmail.smtp.ssl.enable}") String ssl,
                                                @Value("${gmail.smtp.host}") String host,
                                                @Value("${gmail.smtp.port}") String port) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(from);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", debug);
        props.put("smtp.ssl.enable", ssl);

        return mailSender;
    }*/
}