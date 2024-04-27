package com.lukian.userapi.dto;

import com.lukian.userapi.validation.email.EmailConstraint;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequestDto(
        @EmailConstraint
        @NotBlank
        String email
) {
}
