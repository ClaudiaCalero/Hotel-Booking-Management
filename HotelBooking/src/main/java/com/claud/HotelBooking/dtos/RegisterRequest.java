package com.claud.HotelBooking.dtos;

import com.claud.HotelBooking.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "FirstName is required")
    private String firstName;

    @NotBlank(message = "LastName is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "PhoneNumber is required")
    private String phoneNumber;

    private UserRole role;

    @NotBlank(message = "Password is required")
    private String password;

}
