package com.lukian.userapi.service.impl;

import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.exception.RegistrationException;
import com.lukian.userapi.mapper.UserMapper;
import com.lukian.userapi.model.User;
import com.lukian.userapi.repository.UserRepository;
import com.lukian.userapi.service.UserService;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${user.min-age}")
    private int minUserAge;

    @Override
    public UserResponseDto save(UserRegisterRequestDto requestDto) {
        validateAge(requestDto);
        User user = userMapper.toModel(requestDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUserEmailById(Long id, UpdateUserRequestDto requestDto) {
        User userFromDb = getUserFromDb(id);
        userMapper.updateFromDto(requestDto, userFromDb);
        return userMapper.toDto(userRepository.save(userFromDb));
    }

    @Override
    public UserResponseDto updateUserDataById(Long id, UserRegisterRequestDto requestDto) {
        validateAge(requestDto);
        User userFromDb = getUserFromDb(id);
        userMapper.updateFromDto(requestDto, userFromDb);
        return userMapper.toDto(userRepository.save(userFromDb));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(getUserFromDb(id).getId());
    }

    @Override
    public List<UserResponseDto> searchByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        return userRepository.findAllByBirthDateBetween(fromDate, toDate).stream()
                .map(userMapper::toDto)
                .toList();
    }

    private void validateAge(UserRegisterRequestDto requestDto) {
        LocalDate currentDate = LocalDate.now();
        LocalDate userBirthDate = requestDto.birthDate();
        Period userAge = Period.between(userBirthDate, currentDate);

        if (userAge.getYears() < minUserAge) {
            throw new RegistrationException(
                    "User must be at least "
                            + minUserAge
                            + "y.o. to be able to register");
        }
    }

    private User getUserFromDb(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find user by id: " + id));
    }
}

