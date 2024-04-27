package com.lukian.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UserRegisterRequestDto(
        Long id,
        @NotBlank
        @NotNull
        String email,
        @NotBlank
        @NotNull
        String firstName,
        @NotBlank
        @NotNull
        String lastName,
        @NotBlank
        @NotNull
        LocalDate birthDate,
        String address,
        String phoneNumber
) {
}
