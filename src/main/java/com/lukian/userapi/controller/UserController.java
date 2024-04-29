package com.lukian.userapi.controller;

import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User management", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user",
            description = "Create user, validate and save to DB")
    public UserResponseDto registerUser(@RequestBody @Valid UserRegisterRequestDto requestDto) {
        return userService.save(requestDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update email",
            description = "Update user email field")
    public ResponseEntity<UserResponseDto> updateUserEmailById(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUserEmailById(id, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Updates all user data by the specified user ID.
     *
     * updateCarById method uses UserRegisterRequestDto
     * as param since we are updating all the fields,
     * hence there is no need to create extra DTO;
     * UserRegisterRequestDto is well suited for this.
     *
     * @param id user id
     * @param requestDto containing the updated user information
     * @return updated user
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user",
            description = "Updates all user data in fields")
    public ResponseEntity<UserResponseDto> updateCarById(
            @PathVariable Long id,
            @Valid @RequestBody UserRegisterRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUserDataById(id, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user",
            description = "Deletes user from DB by specified ID")
    public ResponseEntity<Void> deleteCarById(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search user",
            description = "Searches user by given range of birth dates")
    public List<UserResponseDto> getUsersByBirthDateRange(
            @RequestParam("from") LocalDate fromDate,
            @RequestParam("to") LocalDate toDate) {
        return userService.searchByBirthDateRange(fromDate, toDate);
    }
}
