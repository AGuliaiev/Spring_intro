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
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
