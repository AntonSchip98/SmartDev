package it.schipani.config;

import io.jsonwebtoken.JwtException;
import it.schipani.businessLayer.security.ApplicationUserDetailsService;
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

            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7); // Extract the token by removing "Bearer "
                log.info("Token: {}", token);

                // Validate the token
                if (!jwt.isTokenValid(token)) {
                    throw new JwtException("Invalid token");
                }

                // Extract username from the token
                String username = jwt.getUsernameFromToken(token);
                log.info("Username: {}", username);

                // Load user details using the username
                var userDetails = userDetailsService.loadUserByUsername(username);
                log.info("UserDetails: {}", userDetails);

                // Create authentication token
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.info("No Authorization header or header does not start with Bearer");
            }
        } catch (Exception e) {
            log.error("Exception in AuthTokenFilter", e);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
