package com.lukian.userapi.service.impl;

import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.exception.RegistrationException;
import com.lukian.userapi.mapper.UserMapper;
import com.lukian.userapi.model.User;
import com.lukian.userapi.repository.UserRepository;
import com.lukian.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Value("${user.min-age}")
    private int minUserAge;

    @BeforeEach
    void setUp() {
        // Reset mock invocations before each test
        reset(userRepository, userMapper);
    }

    @Test
    void testSave_ValidUserRequestDto_Success() {
        // Given
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe", LocalDate.of(1990, 1, 1),
                "Address", "123456789");

        // Mocking user data
        String updatedEmail = "updated.email@gmail.com";

        User user = new User();
        user.setId(1L); // Assuming user ID is set to 1
        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setBirthDate(requestDto.birthDate());
        user.setAddress(requestDto.address());
        user.setPhoneNumber(requestDto.phoneNumber());

        User updatedUser = new User();
        updatedUser.setId(1L);
        user.setEmail(updatedEmail);
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setBirthDate(requestDto.birthDate());
        user.setAddress(requestDto.address());
        user.setPhoneNumber(requestDto.phoneNumber());

        // Mocking user response data
        UserResponseDto expectedResponse = new UserResponseDto(
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getBirthDate(), user.getAddress(), user.getPhoneNumber());

        // Mocking repository and mapper behavior
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        // When
        UserResponseDto actualResponse = userService.save(requestDto);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(userMapper, times(1)).toModel(requestDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void updateUserEmailById_WithValidIdAndDto_ShouldReturnUpdatedUserDto() {
        // Given
        Long testId = 1L;
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto("new.email@example.com");

        User existingUser = new User();
        existingUser.setId(testId);
        existingUser.setEmail("old.email@example.com");
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setBirthDate(LocalDate.of(1990, 1, 1));
        existingUser.setAddress("Address");
        existingUser.setPhoneNumber("123456789");

        User updatedUser = new User();
        updatedUser.setId(testId);
        updatedUser.setEmail(requestDto.email());
        updatedUser.setFirstName(existingUser.getFirstName());
        updatedUser.setLastName(existingUser.getLastName());
        updatedUser.setBirthDate(existingUser.getBirthDate());
        updatedUser.setAddress(existingUser.getAddress());
        updatedUser.setPhoneNumber(existingUser.getPhoneNumber());

        when(userRepository.findById(testId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(updatedUser)).thenReturn(new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getBirthDate(),
                updatedUser.getAddress(),
                updatedUser.getPhoneNumber()
        ));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // When
        UserResponseDto resultDto = userService.updateUserEmailById(testId, requestDto);

        // Then
        assertNotNull(resultDto); // Ensure result is not null
        assertEquals(requestDto.email(), resultDto.email()); // Check if email is updated
        verify(userRepository, times(1)).findById(testId);
        verify(userRepository, times(1)).save(existingUser);


        // Add more tests for other methods such as updateUserEmailById, updateUserDataById, deleteById, searchByBirthDateRange
    }

    @Test
    void updateUserDataById_WithValidIdAndDto_ShouldReturnUpdatedUserDto() {
        // Given
        Long testId = 1L;
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "new.email@example.com", "NewFirstName", "NewLastName",
                LocalDate.of(1995, 5, 15), "NewAddress", "987654321");

        User existingUser = new User();
        existingUser.setId(testId);
        existingUser.setEmail("old.email@example.com");
        existingUser.setFirstName("OldFirstName");
        existingUser.setLastName("OldLastName");
        existingUser.setBirthDate(LocalDate.of(1990, 1, 1));
        existingUser.setAddress("OldAddress");
        existingUser.setPhoneNumber("123456789");

        User updatedUser = new User();
        updatedUser.setId(testId);
        updatedUser.setEmail(requestDto.email());
        updatedUser.setFirstName(requestDto.firstName());
        updatedUser.setLastName(requestDto.lastName());
        updatedUser.setBirthDate(requestDto.birthDate());
        updatedUser.setAddress(requestDto.address());
        updatedUser.setPhoneNumber(requestDto.phoneNumber());

        when(userRepository.findById(testId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(updatedUser)).thenReturn(new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getBirthDate(),
                updatedUser.getAddress(),
                updatedUser.getPhoneNumber()
        ));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // When
        UserResponseDto resultDto = userService.updateUserDataById(testId, requestDto);

        // Then
        assertEquals(updatedUser.getEmail(), resultDto.email());
        assertEquals(updatedUser.getFirstName(), resultDto.firstName());
        assertEquals(updatedUser.getLastName(), resultDto.lastName());
        assertEquals(updatedUser.getBirthDate(), resultDto.birthDate());
        assertEquals(updatedUser.getAddress(), resultDto.address());
        assertEquals(updatedUser.getPhoneNumber(), resultDto.phoneNumber());
        verify(userRepository, times(1)).findById(testId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void deleteById_WithValidId_ShouldDeleteUser() {
        // Given
        Long testId = 1L;
        User userToDelete = new User();
        userToDelete.setId(testId);
        userToDelete.setEmail("old.email@example.com");
        userToDelete.setFirstName("OldFirstName");
        userToDelete.setLastName("OldLastName");
        userToDelete.setBirthDate(LocalDate.of(1990, 1, 1));
        userToDelete.setAddress("OldAddress");
        userToDelete.setPhoneNumber("123456789");

        // Mocking repository behavior
        when(userRepository.findById(testId)).thenReturn(Optional.of(userToDelete));

        // When
        userService.deleteById(testId);

        // Then
        verify(userRepository, times(1)).findById(testId);
        verify(userRepository, times(1)).deleteById(testId);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
    @Test
    void searchByBirthDateRange_WithValidRange_ShouldReturnListOfUsers() {
        // Given
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(1995, 12, 31);

        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("john.doe@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setBirthDate(LocalDate.of(1992, 5, 15));
        user1.setAddress("Address 1");
        user1.setPhoneNumber("123456789");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("jane.doe@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setBirthDate(LocalDate.of(1994, 10, 20));
        user2.setAddress("Address 2");
        user2.setPhoneNumber("987654321");

        List<User> userList = Arrays.asList(user1, user2);

        when(userRepository.findAllByBirthDateBetween(fromDate, toDate)).thenReturn(userList);

        UserResponseDto userResponseDto1 = new UserResponseDto(1L, "john.doe@example.com", "John", "Doe", LocalDate.of(1992, 5, 15), "Address 1", "123456789");
        UserResponseDto userResponseDto2 = new UserResponseDto(2L, "jane.doe@example.com", "Jane", "Doe", LocalDate.of(1994, 10, 20), "Address 2", "987654321");

        when(userMapper.toDto(user1)).thenReturn(userResponseDto1);
        when(userMapper.toDto(user2)).thenReturn(userResponseDto2);

        // When
        List<UserResponseDto> result = userService.searchByBirthDateRange(fromDate, toDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userResponseDto1, result.get(0));
        assertEquals(userResponseDto2, result.get(1));

        verify(userRepository, times(1)).findAllByBirthDateBetween(fromDate, toDate);
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, times(1)).toDto(user2);
    }
}