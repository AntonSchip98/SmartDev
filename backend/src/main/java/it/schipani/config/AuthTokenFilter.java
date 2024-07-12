package it.schipani.config;

import it.schipani.businessLayer.security.ApplicationUserDetailsService;
import it.schipani.businessLayer.security.SecurityUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Tutta la logica di autenticazione basata su token JWT è centralizzata in un'unica classe.
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwt;

    @Autowired
    private ApplicationUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Processing AuthTokenFilter");
            // legge lo header
            // Authorization: Bearer TOKEN
            var header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                log.info("Token: {}", token);
                String username = jwt.getUsernameFromToken(token);
                log.info("Username: {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityUserDetails userDetails = (SecurityUserDetails) userDetailsService.loadUserByUsername(username);
                    log.info("Details: {}", userDetails);

                    if (jwt.isTokenValid(token)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception in auth filter", e);
        }

        filterChain.doFilter(request, response);
    }
}
