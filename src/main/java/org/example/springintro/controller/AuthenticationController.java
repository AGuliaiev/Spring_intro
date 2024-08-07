package org.example.springintro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.UserRegistrationRequestDto;
import org.example.springintro.dto.UserResponseDto;
import org.example.springintro.exception.RegistrationException;
import org.example.springintro.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (!requestDto.getPassword().equals(requestDto.getRepeatPassword())) {
            throw new RegistrationException("Passwords do not match");
        }
        return userService.register(requestDto);
    }
}
