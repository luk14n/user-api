package com.lukian.userapi.dto;

import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        String phoneNumber
) {
}
