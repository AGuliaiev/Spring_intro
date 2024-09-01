package org.example.springintro.services.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.user.UserRegistrationRequestDto;
import org.example.springintro.dto.user.UserResponseDto;
import org.example.springintro.exception.RegistrationException;
import org.example.springintro.mapper.UserMapper;
import org.example.springintro.model.Role;
import org.example.springintro.model.User;
import org.example.springintro.repository.user.RoleRepository;
import org.example.springintro.repository.user.UserRepository;
import org.example.springintro.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findByRole(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("User Role not found"));
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
