package com.lukian.userapi.dto;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String birthDate,
        String address,
        String phoneNumber
) {
}
