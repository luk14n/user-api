package com.lukian.userapi.repository;

import com.lukian.userapi.model.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves user within chosen birthday range.
     *
     * Method is resource friendly since it fetches only those who fall within provided range,
     * without need to fetch all the users from DB, while using JPA query capabilities.
     *
     * @param fromDate The starting date of the birthdate range.
     * @param toDate The ending date of the birthdate range.
     * @return A list of users whose birthdate falls within the specified range.
     */
    List<User> findAllByBirthDateBetween(LocalDate fromDate, LocalDate toDate);
}
