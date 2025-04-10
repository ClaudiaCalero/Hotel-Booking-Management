package com.claud.HotelBooking.entities;

import com.claud.HotelBooking.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Email is requested")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is requested")
    private String password;
    private String firstName;
    private String lastName;

    @NotBlank(message = "Phone Number is requested")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role; //Customer, Admin

    private Boolean isActive;
    private final LocalDateTime createdAt = LocalDateTime.now();


}
