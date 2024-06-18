package it.schipani.businessLayer.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.schipani.businessLayer.dto.LoginUserDto;
import it.schipani.businessLayer.dto.RegisterUserDto;
import it.schipani.businessLayer.dto.RegisteredUserDto;
import it.schipani.businessLayer.exceptions.FileSizeExceededException;
import it.schipani.businessLayer.exceptions.InvalidLoginException;
import it.schipani.businessLayer.services.UserService;
import it.schipani.config.JwtUtils;
import it.schipani.dataLayer.entities.User;
import it.schipani.dataLayer.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder encoder;
    private final UserRepository usersRepository;
    private final AuthenticationManager auth;
    private final JwtUtils jwt;


    @Override
    public RegisteredUserDto register(RegisterUserDto user) {
        if (usersRepository.existsByUsername(user.getUsername())) {
            throw new EntityExistsException("Utente già esistente");
        }
        if (usersRepository.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("Email già registrata");
        }
        try {
            User u = new User();
            BeanUtils.copyProperties(user, u);
            String encryptedPassword = encoder.encode(user.getPassword());
            log.info("Password encrypted: {}", encryptedPassword);
            u.setPassword(encryptedPassword);
            usersRepository.save(u);
            RegisteredUserDto response = new RegisteredUserDto();
            BeanUtils.copyProperties(u, response);
            return response;
        } catch (Exception e) {
            log.error(String.format("Exception saving user %s", user), e);
            throw new PersistenceException(String.valueOf(user));
        }    }

    @Override
    public Optional<LoginUserDto> login(String username, String password) {
        try {
            var authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var user = usersRepository.findOneByUsername(username).orElseThrow();
            LoginUserDto dto = new LoginUserDto();
            dto.setToken(jwt.generateToken(authentication));
            RegisteredUserDto registeredUser = new RegisteredUserDto();
            BeanUtils.copyProperties(user, registeredUser);
            dto.setUsername(registeredUser.getUsername());
            return Optional.of(dto);
        } catch (NoSuchElementException e) {
            log.error("User not found", e);
            throw new InvalidLoginException(username, password);
        } catch (AuthenticationException e) {
            log.error("Authentication failed", e);
            throw new InvalidLoginException(username, password);
        }
    }

    @Override
    public Optional<RegisteredUserDto> get(long id) {
        try {
            User user = usersRepository.findById(id).orElseThrow();
            RegisteredUserDto dto = new RegisteredUserDto();
            BeanUtils.copyProperties(user, dto);
            return Optional.of(dto);
        } catch (Exception e) {
            log.error(String.format("User not found for id %s", id), e);
            return Optional.empty();
        }
    }


}
