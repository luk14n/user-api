package com.lukian.userapi.dto;

import com.lukian.userapi.validation.email.EmailConstraint;
import jakarta.validation.constraints.NotBlank;

/**
 * The email field is annotated with custom annotation {@link EmailConstraint}
 * to avoid repetition of validation logic across multiple fields.
 * <p>
 * If there is no need for custom approach and repetition avoidance, the standard
 * {@link jakarta.validation.constraints.Email} annotation can be used directly like in this case.
 * (like it is demonstrated in {@link com.lukian.userapi.dto.UserRegisterRequestDto})
 */
public record UpdateUserRequestDto(
        @EmailConstraint
        @NotBlank
        String email
) {
}
