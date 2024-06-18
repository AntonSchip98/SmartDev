package it.schipani.businessLayer.impl;

import it.schipani.businessLayer.dto.LoginUserDto;
import it.schipani.businessLayer.dto.RegisterUserDto;
import it.schipani.businessLayer.dto.RegisteredUserDto;
import it.schipani.businessLayer.exceptions.InvalidLoginException;
import it.schipani.businessLayer.services.UserService;
import it.schipani.config.JwtUtils;
import it.schipani.dataLayer.entities.User;
import it.schipani.dataLayer.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final PasswordEncoder encoder;

    @Autowired
    private final UserRepository usersRepository;

    @Autowired
    private final AuthenticationManager auth;

    @Autowired
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
        }
    }

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
    @ResponseStatus(code = HttpStatus.OK)
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

    @Override
    public Optional<RegisteredUserDto> update(long id, RegisterUserDto user) {
        try {
            User existingUser = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            if (!existingUser.getUsername().equals(user.getUsername()) && usersRepository.existsByUsername(user.getUsername())) {
                throw new EntityExistsException("Username already exists");
            }
            if (!existingUser.getEmail().equals(user.getEmail()) && usersRepository.existsByEmail(user.getEmail())) {
                throw new EntityExistsException("Email already exists");
            }
            BeanUtils.copyProperties(user, existingUser, "id", "password");
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String encryptedPassword = encoder.encode(user.getPassword());
                log.info("Password encrypted: {}", encryptedPassword);
                existingUser.setPassword(encryptedPassword);
            }
            usersRepository.save(existingUser);
            RegisteredUserDto dto = new RegisteredUserDto();
            BeanUtils.copyProperties(existingUser, dto);
            return Optional.of(dto);
        } catch (EntityNotFoundException e) {
            log.error(String.format("User not found for id %s", id), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error(String.format("Exception updating user %s", user), e);
            throw new PersistenceException(String.valueOf(user));
        }
    }

    @Override
    @Transactional
    public void delete(long id) {
        try {
            User user = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            usersRepository.delete(user);
        } catch (EntityNotFoundException e) {
            log.error(String.format("User not found for id %s", id), e);
            throw e;
        } catch (Exception e) {
            log.error(String.format("Exception deleting user with id %s", id), e);
            throw new PersistenceException(String.valueOf(id));
        }
    }


}
