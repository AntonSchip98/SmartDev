package it.schipani.businessLayer.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.jsonwebtoken.Claims;
import it.schipani.businessLayer.dto.UserDto.LoginUserDto;
import it.schipani.businessLayer.dto.UserDto.RegisterUserDto;
import it.schipani.businessLayer.dto.UserDto.RegisteredUserDto;
import it.schipani.businessLayer.dto.UserDto.UpdateUserDto;
import it.schipani.businessLayer.exceptions.InvalidLoginException;
import it.schipani.businessLayer.services.UserService;
import it.schipani.config.JwtUtils;
import it.schipani.dataLayer.entities.Identity;
import it.schipani.dataLayer.entities.User;
import it.schipani.dataLayer.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder encoder;

    private final UserRepository usersRepository;

    private final AuthenticationManager auth;

    private final JwtUtils jwt;

    private final Cloudinary cloudinary;



    @Override
    public RegisteredUserDto register(RegisterUserDto user) {
        if (usersRepository.existsByUsername(user.getUsername())) {
            log.warn("Attempted to register existing username: {}", user.getUsername());
            throw new EntityExistsException("Utente già esistente");
        }
        if (usersRepository.existsByEmail(user.getEmail())) {
            log.warn("Attempted to register existing email: {}", user.getEmail());
            throw new EntityExistsException("Email già registrata");
        }
        try {
            User u = new User();
            BeanUtils.copyProperties(user, u);
            String encryptedPassword = encoder.encode(user.getPassword());
            log.info("Password encrypted: {}", encryptedPassword);
            u.setPassword(encryptedPassword);
            usersRepository.save(u);

            log.info("User registered successfully: {}", u.getUsername());
            return RegisteredUserDto.builder()
                    .withId(u.getId())
                    .withUsername(u.getUsername())
                    .withEmail(u.getEmail())
                    .withAvatar(u.getAvatar())
                    .withIdentities(new ArrayList<>())
                    .build();
        } catch (Exception e) {
            log.error(String.format("Exception saving user %s", user), e);
            throw new PersistenceException(String.valueOf(user));
        }
    }

    @Override
    public Optional<LoginUserDto> login(String username, String password) {
        try {
            log.info("Attempting login for username: {}", username);
            var authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var user = usersRepository.findOneByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
            log.info("User found and authenticated: {}", username);

            return Optional.of(LoginUserDto.builder()
                    .withId(user.getId())
                    .withUsername(user.getUsername())
                    .withEmail(user.getEmail())
                    .withToken(jwt.generateToken(authentication))
                    .build());
        } catch (NoSuchElementException e) {
            log.error("User not found during login attempt: {}", username, e);
            throw new InvalidLoginException(username, password);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for username: {}", username, e);
            throw new InvalidLoginException(username, password);
        }
    }

    @Override
    public Optional<RegisteredUserDto> get(long id) {
        try {
            User user = usersRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            RegisteredUserDto dto = RegisteredUserDto.builder()
                    .withId(user.getId())
                    .withUsername(user.getUsername())
                    .withEmail(user.getEmail())
                    .withAvatar(user.getAvatar())
                    .withIdentities(user.getIdentities().stream()
                            .map(Identity::getId).collect(Collectors.toList()))
                    .build();

            log.info("Retrieved user with id: {}", id);
            return Optional.of(dto);
        } catch (EntityNotFoundException e) {
            log.error(String.format("User not found for id %s", id), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error(String.format("Exception retrieving user with id %s", id), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<UpdateUserDto> update(long id, UpdateUserDto userDto) {
        try {
            User existingUser = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            if (!existingUser.getUsername().equals(userDto.getUsername()) && usersRepository.existsByUsername(userDto.getUsername())) {
                log.warn("Attempted to update to existing username: {}", userDto.getUsername());
                throw new EntityExistsException("Username already exists");
            }
            if (!existingUser.getEmail().equals(userDto.getEmail()) && usersRepository.existsByEmail(userDto.getEmail())) {
                log.warn("Attempted to update to existing email: {}", userDto.getEmail());
                throw new EntityExistsException("Email already exists");
            }
            BeanUtils.copyProperties(userDto, existingUser, "id");

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                String encryptedPassword = encoder.encode(userDto.getPassword());
                log.info("Password encrypted: {}", encryptedPassword);
                existingUser.setPassword(encryptedPassword);
            }

            usersRepository.save(existingUser);
            log.info("User updated successfully: {}", existingUser.getUsername());

            Authentication authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(existingUser.getUsername(), userDto.getPassword() != null ? userDto.getPassword() : existingUser.getPassword()));
            String newToken = jwt.generateToken(authentication);

            var dto = UpdateUserDto.builder()
                    .withUsername(existingUser.getUsername())
                    .withEmail(existingUser.getEmail())
                    .withAvatar(existingUser.getAvatar())
                    .withToken(newToken)
                    .build();

            return Optional.of(dto);
        } catch (EntityNotFoundException e) {
            log.error(String.format("User not found for id %s", id), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error(String.format("Exception updating user %s", userDto), e);
            throw new PersistenceException(String.valueOf(userDto));
        }
    }

    @Override
    public void delete(long id) {
        try {
            User user = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            usersRepository.delete(user);
            log.info("Deleted user with id: {}", id);
        } catch (EntityNotFoundException e) {
            log.error(String.format("User not found for id %s", id), e);
            throw e;
        } catch (Exception e) {
            log.error(String.format("Exception deleting user with id %s", id), e);
            throw new PersistenceException(String.valueOf(id));
        }
    }

    @Override
    public void updateAvatar(long id, MultipartFile avatar) throws IOException {
        try {
            User user = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
            var uploadResult = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            String avatarUrl = (String) uploadResult.get("url");
            user.setAvatar(avatarUrl);
            usersRepository.save(user);
        } catch (IOException e) {
            log.error("Error uploading avatar", e);
            throw e;
        }
    }
}
