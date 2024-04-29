package com.lukian.userapi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.mapper.UserMapper;
import com.lukian.userapi.model.User;
import com.lukian.userapi.repository.UserRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

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
        reset(userRepository, userMapper);
    }

    @Test
    void testSave_ValidUserRequestDto_Success() {
        // Given
        UserRegisterRequestDto requestDto = createUserRegisterRequestDto(
                "john.doe@example.com", "John", "Doe",
                LocalDate.of(1990, 1, 1), "Address", "123456789");

        User user = createUserFromDto(requestDto);

        UserResponseDto expectedResponse = createUserResponseDto(user);

        mockSaveUser(requestDto, user, expectedResponse);

        // When
        UserResponseDto actualResponse = userService.save(requestDto);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verifySaveUserInteraction(requestDto, user, expectedResponse);
    }

    @Test
    void updateUserEmailById_WithValidIdAndDto_ShouldReturnUpdatedUserDto() {
        // Given
        Long testId = 1L;

        User existingUser = createUser("old.email@example.com", "John", "Doe",
                LocalDate.of(1990, 1, 1), "Address", "123456789");

        UpdateUserRequestDto requestDto = new UpdateUserRequestDto("new.email@example.com");

        User updatedUser = createUser(requestDto.email(), existingUser.getFirstName(),
                existingUser.getLastName(), existingUser.getBirthDate(),
                existingUser.getAddress(), existingUser.getPhoneNumber());

        when(userRepository.findById(testId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(updatedUser)).thenReturn(createUserResponseDto(updatedUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // When
        UserResponseDto resultDto = userService.updateUserEmailById(testId, requestDto);

        // Then
        assertNotNull(resultDto); // Ensure result is not null
        assertEquals(requestDto.email(), resultDto.email()); // Check if email is updated
        verify(userRepository, times(1)).findById(testId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUserDataById_WithValidIdAndDto_ShouldReturnUpdatedUserDto() {
        // Given
        Long testId = 1L;

        User existingUser = createUser("old.email@example.com", "OldFirstName", "OldLastName",
                LocalDate.of(1990, 1, 1), "OldAddress", "123456789");

        UserRegisterRequestDto requestDto = createUserRegisterRequestDto("new.email@example.com",
                "NewFirstName", "NewLastName",
                LocalDate.of(1995, 5, 15), "NewAddress", "987654321");

        User updatedUser = createUser(requestDto.email(), requestDto.firstName(),
                requestDto.lastName(), requestDto.birthDate(),
                requestDto.address(), requestDto.phoneNumber());

        when(userRepository.findById(testId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(updatedUser)).thenReturn(createUserResponseDto(updatedUser));
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
        List<User> userList = Arrays.asList(
                createUser("john.doe@example.com", "John", "Doe",
                        LocalDate.of(1992, 5, 15), "Address 1", "123456789"),
                createUser("jane.doe@example.com", "Jane", "Doe",
                        LocalDate.of(1994, 10, 20), "Address 2", "987654321")
        );

        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(1995, 12, 31);

        when(userRepository.findAllByBirthDateBetween(fromDate, toDate)).thenReturn(userList);

        List<UserResponseDto> expectedResponse = Arrays.asList(
                createUserResponseDto(userList.get(0)),
                createUserResponseDto(userList.get(1))
        );

        when(userMapper.toDto(userList.get(0))).thenReturn(expectedResponse.get(0));
        when(userMapper.toDto(userList.get(1))).thenReturn(expectedResponse.get(1));

        // When
        List<UserResponseDto> result = userService.searchByBirthDateRange(fromDate, toDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResponse, result);
        verify(userRepository, times(1)).findAllByBirthDateBetween(fromDate, toDate);
        verify(userMapper, times(1)).toDto(userList.get(0));
        verify(userMapper, times(1)).toDto(userList.get(1));
    }

    private User createUser(String email, String firstName, String lastName, LocalDate birthDate,
                            String address, String phoneNumber) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthDate(birthDate);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    private UserResponseDto createUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getBirthDate(), user.getAddress(), user.getPhoneNumber());
    }

    private UserRegisterRequestDto createUserRegisterRequestDto(
            String email, String firstName, String lastName,
            LocalDate birthDate, String address, String phoneNumber) {
        return new UserRegisterRequestDto(
                email, firstName, lastName, birthDate, address, phoneNumber);
    }

    private User createUserFromDto(UserRegisterRequestDto requestDto) {
        User user = new User();
        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setBirthDate(requestDto.birthDate());
        user.setAddress(requestDto.address());
        user.setPhoneNumber(requestDto.phoneNumber());
        return user;
    }

    private void mockSaveUser(
            UserRegisterRequestDto requestDto, User user, UserResponseDto expectedResponse) {
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);
    }

    private void verifySaveUserInteraction(
            UserRegisterRequestDto requestDto, User user, UserResponseDto expectedResponse) {
        verify(userMapper, times(1)).toModel(requestDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }
}
