package org.example.springintro.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.springintro.validation.FieldMatch;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(
        first = "password",
        second = "repeatPassword",
        message = "The password fields must match"
)
public class UserRegistrationRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 6, max = 20)
    private String password;
    @NotBlank
    @Length(min = 6, max = 20)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
