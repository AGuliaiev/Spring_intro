package org.example.springintro.util;

import org.example.springintro.dto.user.UserLoginRequestDto;
import org.example.springintro.dto.user.UserRegistrationRequestDto;

public class UserTestUtils {
    private static final String HASHED_PASSWORD = "hashedpassword789";
    private static final String EXISTING_EMAIL = "john.doe@example.com";

    public static UserLoginRequestDto createLoginRequest(String password) {
        return new UserLoginRequestDto(EXISTING_EMAIL, password);
    }

    public static UserRegistrationRequestDto createRegistrationRequest(
            String email,
            String firstName,
            String lastName,
            String address
    ) {
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setEmail(email);
        dto.setPassword(HASHED_PASSWORD);
        dto.setRepeatPassword(HASHED_PASSWORD);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setShippingAddress(address);
        return dto;
    }
}
