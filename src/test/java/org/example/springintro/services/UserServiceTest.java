package org.example.springintro.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.springintro.dto.user.UserRegistrationRequestDto;
import org.example.springintro.dto.user.UserResponseDto;
import org.example.springintro.exception.RegistrationException;
import org.example.springintro.mapper.UserMapper;
import org.example.springintro.model.Role;
import org.example.springintro.model.User;
import org.example.springintro.repository.user.RoleRepository;
import org.example.springintro.repository.user.UserRepository;
import org.example.springintro.services.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ShoppingCartService shoppingCartService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("register() - Given existing email,"
            + " When registering user, Then throws RegistrationException")
    public void register_UserAlreadyExists_ThrowsRegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("Password123");
        requestDto.setRepeatPassword("Password123");
        requestDto.setFirstName("Test");
        requestDto.setLastName("User");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(requestDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("Email already in use");

        verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName(""" 
            register() - Given valid user data,
            When registering user,
            Then returns UserResponseDto
            """)
    public void register_ValidUser_ReturnsUserResponseDto() {
        // Given
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("Password123");
        requestDto.setRepeatPassword("Password123");
        requestDto.setFirstName("Test");
        requestDto.setLastName("User");

        User user = new User();
        user.setId(1L);
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());

        Role role = new Role();
        role.setId(1L);
        role.setRole(Role.RoleName.USER);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.register(requestDto);
        assertThat(result).isEqualTo(userResponseDto);

        verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(userMapper, times(1)).toModel(requestDto);
        verify(passwordEncoder, times(1)).encode(requestDto.getPassword());
        verify(roleRepository, times(1)).findByRole(Role.RoleName.USER);
        verify(userRepository, times(1)).save(user);
        verify(shoppingCartService, times(1)).createShoppingCart(user);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository);
    }
}
