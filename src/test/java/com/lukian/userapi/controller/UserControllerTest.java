package com.lukian.userapi.controller;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.exception.RegistrationException;
import com.lukian.userapi.service.UserService;
import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserRegisterRequestDto requestDto = createUserRequestDto();
        UserResponseDto userResponseDto = createUserResponseDto(requestDto, 1L);
        performPostRequestAndVerify(requestDto, userResponseDto);
    }

    @Test
    public void testRegisterUser_UnderMinimumAge() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe",
                LocalDate.now().minusYears(17),
                "Address", "123456789");

        when(userService.save(any(UserRegisterRequestDto.class)))
                .thenThrow(new RegistrationException(
                        "User must be at least 18y.o. to be able to register"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value(
                        "User must be at least 18y.o. to be able to register"));
    }

    @Test
    public void testUpdateUserEmailById_Success() throws Exception {
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = createUserRequestDto();
        UserResponseDto createdUserResponseDto =
                createUserResponseDto(createUserRequestDto, userId);
        when(userService.save(any(UserRegisterRequestDto.class)))
                .thenReturn(createdUserResponseDto);

        String newEmail = "new.email@example.com";
        UpdateUserRequestDto updateRequestDto = new UpdateUserRequestDto(newEmail);

        UserResponseDto updatedUserResponseDto = new UserResponseDto(
                userId, newEmail, createUserRequestDto.firstName(),
                createUserRequestDto.lastName(),
                createUserRequestDto.birthDate(), createUserRequestDto.address(),
                createUserRequestDto.phoneNumber());
        when(userService.updateUserEmailById(userId, updateRequestDto))
                .thenReturn(updatedUserResponseDto);

        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.firstName").value(createdUserResponseDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(createdUserResponseDto.lastName()))
                .andExpect(jsonPath("$.birthDate").value(
                        createdUserResponseDto.birthDate().toString()))
                .andExpect(jsonPath("$.address").value(createdUserResponseDto.address()))
                .andExpect(jsonPath("$.phoneNumber").value(createdUserResponseDto.phoneNumber()));
    }

    @Test
    public void testUpdateUserEmailById_InvalidEmail() throws Exception {
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = createUserRequestDto();
        UserResponseDto createdUserResponseDto =
                createUserResponseDto(createUserRequestDto, userId);
        when(userService.save(any(UserRegisterRequestDto.class)))
                .thenReturn(createdUserResponseDto);

        String invalidEmail = "invalid.email.com";
        UpdateUserRequestDto updateRequestDto = new UpdateUserRequestDto(invalidEmail);

        mockMvc.perform(patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errors").value("email invalid email format"));
    }

    @Test
    public void testUpdateCarById_Success() throws Exception {
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = createUserRequestDto();
        UserResponseDto createdUserResponseDto =
                createUserResponseDto(createUserRequestDto, userId);
        when(userService.save(any(UserRegisterRequestDto.class)))
                .thenReturn(createdUserResponseDto);

        String newEmail = "new.email@example.com";
        String newFirstName = "Jane";
        String newLastName = "Doe";
        LocalDate newBirthDate = LocalDate.of(1995, 5, 10);
        String newAddress = "New Address";
        String newPhoneNumber = "987654321";
        UserRegisterRequestDto updateRequestDto = new UserRegisterRequestDto(
                newEmail, newFirstName, newLastName, newBirthDate, newAddress, newPhoneNumber);

        UserResponseDto updatedUserResponseDto = new UserResponseDto(
                userId, newEmail, newFirstName, newLastName, newBirthDate,
                newAddress, newPhoneNumber);
        when(userService.updateUserDataById(userId, updateRequestDto))
                .thenReturn(updatedUserResponseDto);

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
        Long userId = 1L;
        UserRegisterRequestDto createUserRequestDto = createUserRequestDto();
        UserResponseDto createdUserResponseDto =
                createUserResponseDto(createUserRequestDto, userId);
        when(userService.save(any(UserRegisterRequestDto.class)))
                .thenReturn(createdUserResponseDto);

        doNothing().when(userService).deleteById(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    public void testGetUsersByBirthDateRange_Success() throws Exception {
        UserRegisterRequestDto user1 = createUserRequestDto();
        UserRegisterRequestDto user2 = createUserRequestDto();
        UserRegisterRequestDto user3 = createUserRequestDto();
        UserRegisterRequestDto user4 = createUserRequestDto();

        UserResponseDto userResponseDto1 = createUserResponseDto(user1, 1L);
        UserResponseDto userResponseDto2 = createUserResponseDto(user2, 2L);
        UserResponseDto userResponseDto3 = createUserResponseDto(user3, 3L);
        UserResponseDto userResponseDto4 = createUserResponseDto(user4, 4L);

        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);
        List<UserResponseDto> usersInRange = List.of(userResponseDto1,
                userResponseDto2, userResponseDto3);
        when(userService.searchByBirthDateRange(fromDate, toDate))
                .thenReturn(usersInRange);

        mockMvc.perform(get("/api/users/search")
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email").value(userResponseDto1.email()))
                .andExpect(jsonPath("$[1].email").value(userResponseDto2.email()))
                .andExpect(jsonPath("$[2].email").value(userResponseDto3.email()));
    }

    private UserRegisterRequestDto createUserRequestDto() {
        return new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe",
                LocalDate.of(1990, 1, 1),
                "Address", "123456789");
    }

    private UserResponseDto createUserResponseDto(UserRegisterRequestDto requestDto, Long id) {
        return new UserResponseDto(
                id, requestDto.email(), requestDto.firstName(),
                requestDto.lastName(), requestDto.birthDate(),
                requestDto.address(), requestDto.phoneNumber());
    }

    private void performPostRequestAndVerify(
            UserRegisterRequestDto requestDto, UserResponseDto userResponseDto) throws Exception {
        when(userService.save(any(UserRegisterRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userResponseDto.email()));
    }
}
