package org.example.springintro.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.UserRegistrationRequestDto;
import org.example.springintro.dto.UserResponseDto;
import org.example.springintro.exception.RegistrationException;
import org.example.springintro.mapper.UserMapper;
import org.example.springintro.model.User;
import org.example.springintro.repository.user.UserRepository;
import org.example.springintro.services.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(requestDto);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
