package it.schipani.presentationLayer.controller;

import com.cloudinary.Cloudinary;
import it.schipani.businessLayer.dto.LoginUserDto;
import it.schipani.businessLayer.dto.RegisterUserDto;
import it.schipani.businessLayer.dto.RegisteredUserDto;
import it.schipani.businessLayer.exceptions.ApiValidationException;
import it.schipani.businessLayer.services.UserService;
import it.schipani.dataLayer.entities.User;
import it.schipani.dataLayer.repositories.UserRepository;
import it.schipani.presentationLayer.model.LoginUserModel;
import it.schipani.presentationLayer.model.RegisterUserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService user;

    @Autowired
    private UserRepository userRepository;



    @PostMapping("register")
    public ResponseEntity<RegisteredUserDto> register(@RequestBody @Validated RegisterUserModel model,
                                                      BindingResult validator) {
        if (validator.hasErrors()) {
            throw new ApiValidationException(validator.getAllErrors());
        }
        var registeredUser = user.register(
                RegisterUserDto.builder()
                        .withUsername(model.username())
                        .withEmail(model.email())
                        .withPassword(model.password())
                        .build());

        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<LoginUserDto> login(@RequestBody @Validated LoginUserModel model, BindingResult validator) {
        if (validator.hasErrors()) {
            throw new ApiValidationException(validator.getAllErrors());
        }
        return new ResponseEntity<>(user.login(model.username(), model.password()).orElseThrow(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public Optional<RegisteredUserDto> get(@PathVariable long id) {
        return user.get(id);
    }

}
