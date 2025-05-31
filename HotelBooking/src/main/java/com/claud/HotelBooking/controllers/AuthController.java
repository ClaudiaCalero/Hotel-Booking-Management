package com.claud.HotelBooking.controllers;

import com.claud.HotelBooking.dtos.LoginRequest;
import com.claud.HotelBooking.dtos.RegistrationRequest;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid RegistrationRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));

    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));

    }

}
