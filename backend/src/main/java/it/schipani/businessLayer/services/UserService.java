package it.schipani.businessLayer.services;

import it.schipani.businessLayer.dto.LoginUserDto;
import it.schipani.businessLayer.dto.RegisterUserDto;
import it.schipani.businessLayer.dto.RegisteredUserDto;

import java.util.Optional;


public interface UserService {
    RegisteredUserDto register(RegisterUserDto user);
    Optional<LoginUserDto> login(String username, String password);
    Optional<RegisteredUserDto> get(long id);
    Optional<RegisteredUserDto> update(long id, RegisterUserDto user);
    void delete(long id);
}
