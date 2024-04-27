package com.lukian.userapi.controller;

import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/create")
    public UserResponseDto registerUser(UserRegisterRequestDto requestDto) {
        return userService.save(requestDto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto updateUserEmailById(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequestDto requestDto) {
        return userService.updateUserEmailById(id, requestDto);
    }

    /**
     * Updates all user data by the specified user ID.
     *
     * updateCarById method uses UserRegisterRequestDto as param since we are updating all the fields,
     * hence there is no need to create extra DTO; UserRegisterRequestDto is well suited for this.
     *
     * @param id
     * @param requestDto containing the updated user information
     * @return updated user
     */
    @PutMapping("/{id}")
    public UserResponseDto updateCarById(@PathVariable Long id,
                                         @Valid @RequestBody UserRegisterRequestDto requestDto) {
        return userService.updateUserDataById(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarById(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<UserResponseDto> getUsersByBirthDateRange(
            @RequestParam("from") LocalDate fromDate,
            @RequestParam("to") LocalDate toDate) {
        return userService.searchByBirthDateRange(fromDate, toDate);
    }
}
