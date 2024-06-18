package it.schipani.businessLayer.services;

import it.schipani.businessLayer.dto.LoginUserDto;
import it.schipani.businessLayer.dto.RegisterUserDto;
import it.schipani.businessLayer.dto.RegisteredUserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


public interface UserService {
    RegisteredUserDto register(RegisterUserDto user);
    Optional<LoginUserDto> login(String username, String password);
    Optional<RegisteredUserDto> get(long id);

}
