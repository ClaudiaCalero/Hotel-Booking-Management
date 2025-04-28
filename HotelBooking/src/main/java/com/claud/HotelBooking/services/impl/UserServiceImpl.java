package com.claud.HotelBooking.services.impl;

import com.claud.HotelBooking.dtos.LoginRequest;
import com.claud.HotelBooking.dtos.RegistrationRequest;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.UserDTO;
import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.exceptions.InvalidCredentialException;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.UserRepository;
import com.claud.HotelBooking.security.JwtUtils;
import com.claud.HotelBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;

    @Override
    public Response registerUser(RegistrationRequest registrationRequest) {
        UserRole role = UserRole.CUSTOMER;

        if (registrationRequest.getRole() != null) {
            role = registrationRequest.getRole();
        }

        User userToSave = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(role)
                .isActive(Boolean.TRUE)
                .build();
        userRepository.save(userToSave);
        return Response.builder()
                .status(200)
                .message("user created successfully")
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email Not Found"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Password doesn't match");
        }
        String token = jwtUtils.generateToken(user.getEmail());

        return Response.builder()
                .status(200)
                .message("user logged in successfully")
                .role(user.getRole())
                .token(token)
                .isActive(user.getIsActive())
                .expirationTime("6 months")
                .build();
    }

    @Override
    public Response getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<UserDTO> userDTOList = modelMapper.map(users, new TypeToken<List<UserDTO>>())
    }

    @Override
    public Response getOwnAccountDetails() {
        return null;
    }

    @Override
    public Response getCurrentLoggedInUser() {
        return null;
    }

    @Override
    public Response updateOwnAccount(UserDTO userDTO) {
        return null;
    }

    @Override
    public Response deleteOwnAccount() {
        return null;
    }

    @Override
    public Response getMyBookingHistory() {
        return null;
    }
}

