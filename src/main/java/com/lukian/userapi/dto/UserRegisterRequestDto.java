package com.lukian.userapi.dto;

import com.lukian.userapi.validation.email.EmailConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * The email field is annotated with {@link jakarta.validation.constraints.Email} to ensure
 * it is a valid email address format. Additionally, the custom annotation
 * {@link EmailConstraint} can be used to avoid repetition
 * of validation logic across multiple fields
 * (like it is demonstrated in {@link com.lukian.userapi.dto.UpdateUserRequestDto}).
 * <p>
 * If there is no need for custom approach and repetition avoidance, the standard
 * {@link jakarta.validation.constraints.Email} annotation can be used directly like in this case.
 */
public record UserRegisterRequestDto(
        @Email(regexp = "^\\S+@\\S+\\.\\S+$")
        String email,
        @NotBlank
        @NotNull
        String firstName,
        @NotBlank
        @NotNull
        String lastName,
        @NotNull
        LocalDate birthDate,
        String address,
        String phoneNumber
) {
}
