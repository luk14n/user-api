package com.lukian.userapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.exception.RegistrationException;
import com.lukian.userapi.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterUser_Success() throws Exception {
        // Prepare a valid request DTO
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe", LocalDate.of(1990, 1, 1),
                "Address", "123456789");

        // Prepare a mock user response
        UserResponseDto userResponseDto = new UserResponseDto(
                1L, requestDto.email(), requestDto.firstName(), requestDto.lastName(), requestDto.birthDate(),
                requestDto.address(), requestDto.phoneNumber());

        // Mock userService.save() method to return the mock user response
        when(userService.save(any(UserRegisterRequestDto.class))).thenReturn(userResponseDto);

        // Perform the POST request to register a user
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        // Verify that the user was registered successfully
        String responseBody = result.getResponse().getContentAsString();
        UserResponseDto registeredUser = objectMapper.readValue(responseBody, UserResponseDto.class);
        assertNotNull(registeredUser);
        assertEquals(userResponseDto.id(), registeredUser.id());
        assertEquals(userResponseDto.email(), registeredUser.email());
        // Similarly, compare other fields
    }

    @Test
    public void testRegisterUser_UnderMinimumAge() throws Exception {
        // Prepare a request DTO with a birthdate that makes the user under the minimum age requirement
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe", LocalDate.now().minusYears(17),
                "Address", "123456789");

        // Mock the userService.save() method
        when(userService.save(any(UserRegisterRequestDto.class))).thenThrow(new RegistrationException(
                "User must be at least 18y.o. to be able to register"));

        // Perform the POST request to register a user
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(
                        "User must be at least 18y.o. to be able to register"));
    }

    @Test
    public void testUpdateUserEmailById_Success() throws Exception {
        // Create a user first
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe", LocalDate.of(1990, 1, 1),
                "Address", "123456789");
        UserResponseDto createdUserResponseDto = new UserResponseDto(
                userId, createUserRequestDto.email(), createUserRequestDto.firstName(), createUserRequestDto.lastName(),
                createUserRequestDto.birthDate(), createUserRequestDto.address(), createUserRequestDto.phoneNumber());
        when(userService.save(any(UserRegisterRequestDto.class))).thenReturn(createdUserResponseDto);

        // Now prepare the request to update the user's email
        String newEmail = "new.email@example.com";
        UpdateUserRequestDto updateRequestDto = new UpdateUserRequestDto(newEmail);

        // Mock userService.updateUserEmailById() method to return the updated user response
        UserResponseDto updatedUserResponseDto = new UserResponseDto(
                userId, newEmail, createUserRequestDto.firstName(), createUserRequestDto.lastName(),
                createUserRequestDto.birthDate(), createUserRequestDto.address(), createUserRequestDto.phoneNumber());
        when(userService.updateUserEmailById(userId, updateRequestDto)).thenReturn(updatedUserResponseDto);

        // Perform the PATCH request to update user email
        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.firstName").value(createdUserResponseDto.firstName())) // Additional assertion for unchanged fields
                .andExpect(jsonPath("$.lastName").value(createdUserResponseDto.lastName())) // Additional assertion for unchanged fields
                .andExpect(jsonPath("$.birthDate").value(createdUserResponseDto.birthDate().toString())) // Additional assertion for unchanged fields
                .andExpect(jsonPath("$.address").value(createdUserResponseDto.address())) // Additional assertion for unchanged fields
                .andExpect(jsonPath("$.phoneNumber").value(createdUserResponseDto.phoneNumber())); // Additional assertion for unchanged fields
    }

    @Test
    public void testUpdateUserEmailById_InvalidEmail() throws Exception {
        // Create a user first
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe", LocalDate.of(1990, 1, 1),
                "Address", "123456789");
        UserResponseDto createdUserResponseDto = new UserResponseDto(
                userId, createUserRequestDto.email(), createUserRequestDto.firstName(), createUserRequestDto.lastName(),
                createUserRequestDto.birthDate(), createUserRequestDto.address(), createUserRequestDto.phoneNumber());
        when(userService.save(any(UserRegisterRequestDto.class))).thenReturn(createdUserResponseDto);

        // Now prepare the request to update the user's email with an invalid email
        String invalidEmail = "invalid.email.com";
        UpdateUserRequestDto updateRequestDto = new UpdateUserRequestDto(invalidEmail);

        // Perform the PATCH request to update user email
        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST")) // Expecting status as string
                .andExpect(jsonPath("$.errors").value("email invalid email format")); // Asserting the error message
    }

    @Test
    public void testUpdateCarById_Success() throws Exception {
        // Create a user first
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe", LocalDate.of(1990, 1, 1), 
                "Address", "123456789");
        UserResponseDto createdUserResponseDto = new UserResponseDto(
                userId, createUserRequestDto.email(), createUserRequestDto.firstName(), createUserRequestDto.lastName(), 
                createUserRequestDto.birthDate(), createUserRequestDto.address(), createUserRequestDto.phoneNumber());
        when(userService.save(any(UserRegisterRequestDto.class))).thenReturn(createdUserResponseDto);

        // Now prepare the request to update the user's data
        String newEmail = "new.email@example.com";
        String newFirstName = "Jane";
        String newLastName = "Doe";
        LocalDate newBirthDate = LocalDate.of(1995, 5, 10);
        String newAddress = "New Address";
        String newPhoneNumber = "987654321";
        UserRegisterRequestDto updateRequestDto = new UserRegisterRequestDto(
                newEmail, newFirstName, newLastName, newBirthDate, newAddress, newPhoneNumber);

        // Mock userService.updateUserDataById() method to return the updated user response
        UserResponseDto updatedUserResponseDto = new UserResponseDto(
                userId, newEmail, newFirstName, newLastName, newBirthDate, newAddress, newPhoneNumber);
        when(userService.updateUserDataById(userId, updateRequestDto)).thenReturn(updatedUserResponseDto);

        // Perform the PUT request to update user data
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.firstName").value(newFirstName))
                .andExpect(jsonPath("$.lastName").value(newLastName))
                .andExpect(jsonPath("$.birthDate").value(newBirthDate.toString()))
                .andExpect(jsonPath("$.address").value(newAddress))
                .andExpect(jsonPath("$.phoneNumber").value(newPhoneNumber));
    }

    @Test
    public void testDeleteCarById_Success() throws Exception {
        // Create a user first
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe", LocalDate.of(1990, 1, 1),
                "Address", "123456789");
        UserResponseDto createdUserResponseDto = new UserResponseDto(
                userId, createUserRequestDto.email(), createUserRequestDto.firstName(), createUserRequestDto.lastName(),
                createUserRequestDto.birthDate(), createUserRequestDto.address(), createUserRequestDto.phoneNumber());
        when(userService.save(any(UserRegisterRequestDto.class))).thenReturn(createdUserResponseDto);

        // Now prepare the request to delete the user
        doNothing().when(userService).deleteById(userId);

        // Perform the DELETE request to delete the user
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        // Verify that the userService.deleteById method was called with the correct user ID
        verify(userService, times(1)).deleteById(userId);
    }
    @Test
    public void testGetUsersByBirthDateRange_Success() throws Exception {
        // Create users with different birth dates
        UserRegisterRequestDto user1 = new UserRegisterRequestDto("user1@example.com", "User1", "Lastname1", LocalDate.of(1990, 1, 1), "Address1", "123456789");
        UserRegisterRequestDto user2 = new UserRegisterRequestDto("user2@example.com", "User2", "Lastname2", LocalDate.of(1995, 6, 15), "Address2", "987654321");
        UserRegisterRequestDto user3 = new UserRegisterRequestDto("user3@example.com", "User3", "Lastname3", LocalDate.of(2000, 9, 20), "Address3", "555555555");
        UserRegisterRequestDto user4 = new UserRegisterRequestDto("user4@example.com", "User4", "Lastname4", LocalDate.of(1985, 3, 10), "Address4", "111111111");

        UserResponseDto userResponseDto1 = new UserResponseDto(1L, "user1@example.com", "User1", "Lastname1", LocalDate.of(1990, 1, 1), "Address1", "123456789");
        UserResponseDto userResponseDto2 = new UserResponseDto(2L, "user2@example.com", "User2", "Lastname2", LocalDate.of(1995, 6, 15), "Address2", "987654321");
        UserResponseDto userResponseDto3 = new UserResponseDto(3L, "user3@example.com", "User3", "Lastname3", LocalDate.of(2000, 9, 20), "Address3", "555555555");
        UserResponseDto userResponseDto4 = new UserResponseDto(4L, "user4@example.com", "User4", "Lastname4", LocalDate.of(1985, 3, 10), "Address4", "111111111");

        // Mock userService.searchByBirthDateRange() method to return users within a specific date range
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);
        List<UserResponseDto> usersInRange = List.of(userResponseDto1, userResponseDto2, userResponseDto3);
        when(userService.searchByBirthDateRange(fromDate, toDate)).thenReturn(usersInRange);

        // Perform the GET request to retrieve users within the specified date range
        mockMvc.perform(get("/api/users/search")
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // Expecting 3 users within the specified date range
                .andExpect(jsonPath("$[0].email").value(userResponseDto1.email())) // Assert user1 fields
                .andExpect(jsonPath("$[1].email").value(userResponseDto2.email())) // Assert user2 fields
                .andExpect(jsonPath("$[2].email").value(userResponseDto3.email())); // Assert user3 fields
    }
}
