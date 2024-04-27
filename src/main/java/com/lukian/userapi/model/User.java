package com.lukian.userapi.model;

import com.lukian.userapi.validation.email.EmailConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.time.LocalDate;

@Entity
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted=false")
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email")
    @EmailConstraint
    @NotBlank
    private String email;
    @Column(name = "first_name", nullable = false)
    @NotBlank
    private String firstName;
    @Column(name = "last_name", nullable = false)
    @NotBlank
    private String LastName;
    @Column(name = "birth_name", nullable = false)
    @NotBlank
    private LocalDate birthDate;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "is_deleted",
            nullable = false)
    private Boolean isDeleted = false;
}

//create update some fields update all