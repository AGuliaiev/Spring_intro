package org.example.springintro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.user.UserLoginRequestDto;
import org.example.springintro.dto.user.UserLoginResponseDto;
import org.example.springintro.dto.user.UserRegistrationRequestDto;
import org.example.springintro.dto.user.UserResponseDto;
import org.example.springintro.exception.RegistrationException;
import org.example.springintro.security.AuthenticationService;
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
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
