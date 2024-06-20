package it.schipani.presentationLayer.controller;

import it.schipani.businessLayer.dto.UserDto.LoginUserDto;
import it.schipani.businessLayer.dto.UserDto.RegisterUserDto;
import it.schipani.businessLayer.dto.UserDto.RegisteredUserDto;
import it.schipani.businessLayer.dto.UserDto.UpdateUserDto;
import it.schipani.businessLayer.services.UserService;
import it.schipani.presentationLayer.model.Requests.LoginUserRequest;
import it.schipani.presentationLayer.model.Requests.RegisterUserRequest;
import it.schipani.presentationLayer.model.Requests.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisteredUserDto> register(@RequestBody RegisterUserRequest userRequest) {
        var registerUserDto = RegisterUserDto.builder()
                .withUsername(userRequest.username())
                .withEmail(userRequest.email())
                .withPassword(userRequest.password())
                .build();

        var registeredUser = userService.register(registerUserDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserDto> login(@RequestBody LoginUserRequest userRequest) {
        Optional<LoginUserDto> loginUser = userService.login(userRequest.username(), userRequest.password());

        return loginUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisteredUserDto> getUser(@PathVariable long id) {
        Optional<RegisteredUserDto> user = userService.get(id);

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserDto> updateUser(@PathVariable long id, @RequestBody UpdateUserRequest userRequest) {
        var updateUserDto = UpdateUserDto.builder()
                .withUsername(userRequest.username())
                .withEmail(userRequest.email())
                .withAvatar(userRequest.avatar())
                .withPassword(userRequest.password()) // Aggiungi il campo password
                .build();

        Optional<UpdateUserDto> updatedUser = userService.update(id, updateUserDto);

        return updatedUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable long id, @RequestParam("avatar") MultipartFile avatar) {
        try {
            userService.updateAvatar(id, avatar);
            return ResponseEntity.ok("Avatar updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading avatar: " + e.getMessage());
        }
    }
}
