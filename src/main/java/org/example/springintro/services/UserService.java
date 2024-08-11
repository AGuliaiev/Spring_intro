package org.example.springintro.services;

import org.example.springintro.dto.UserRegistrationRequestDto;
import org.example.springintro.dto.UserResponseDto;
import org.example.springintro.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
