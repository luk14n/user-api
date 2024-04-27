package com.lukian.userapi.service;

import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    UserResponseDto save(UserRegisterRequestDto requestDto);

    UserResponseDto updateUserEmailById(Long id, UpdateUserRequestDto requestDto);

    UserResponseDto updateUserDataById(Long id, UserRegisterRequestDto requestDto);

    void deleteById(Long id);

    List<UserResponseDto> searchByBirthDateRange(LocalDate fromDate, LocalDate toDate);
}
