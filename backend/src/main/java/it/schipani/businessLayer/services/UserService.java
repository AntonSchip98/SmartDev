package it.schipani.businessLayer.services;

import it.schipani.businessLayer.dto.UserDto.LoginUserDto;
import it.schipani.businessLayer.dto.UserDto.RegisterUserDto;
import it.schipani.businessLayer.dto.UserDto.RegisteredUserDto;
import it.schipani.businessLayer.dto.UserDto.UpdateUserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    RegisteredUserDto register(RegisterUserDto user);
    Optional<LoginUserDto> login(String username, String password);
    Optional<RegisteredUserDto> get(long id);
    Optional<UpdateUserDto> update(long id, UpdateUserDto user);
    void delete(long id);

    void updateAvatar(long id, MultipartFile avatar) throws IOException;
}
