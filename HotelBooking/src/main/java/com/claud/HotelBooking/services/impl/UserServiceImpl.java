package com.claud.HotelBooking.services.impl;

import com.claud.HotelBooking.dtos.*;
import com.claud.HotelBooking.entities.Booking;
import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.exceptions.InvalidCredentialException;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.UserRepository;
import com.claud.HotelBooking.security.JwtUtils;
import com.claud.HotelBooking.services.EmailService;
import com.claud.HotelBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    private final EmailService emailService;
    //Took the master email from the application.properties
    @Value("${hotel.admin.email}")
    private String adminEmail;


    @Override
    public Response registerUser(RegistrationRequest registrationRequest) {

        // 1. Check if the email already exists before proceed
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new InvalidCredentialException("El correo electrónico ya está registrado.");
        }

        // 2. We establish a default base role
        UserRole role = UserRole.CUSTOMER;

        // 3. If the frontend sends an explicit role, it is temporarily respected
        if (registrationRequest.getRole() != null) {
            role = registrationRequest.getRole();
        }

        // 4. MASTER RULE (ALWAYS AT THE END): If the email matches the admin,
        /// we force the ADMIN role no matter what.
        if (registrationRequest.getEmail().equalsIgnoreCase(adminEmail)) {
            role = UserRole.ADMIN;
        }

        // 5.Build and save the user with the final filtered role
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
                .orElseThrow(()-> new NotFoundException("Email Not Found"));

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

        List<UserDTO> userDTOList = modelMapper.map(users, new TypeToken<List<UserDTO>>(){}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .users(userDTOList)
                .build();
    }

    @Override
    public Response getOwnAccountDetails() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User Not Found"));


        log.info("Inside getOwnAccountDetails user email is {}", email);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .user(userDTO)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("User Not Found"));
    }

    @Override
    public Response updateOwnAccount(UserDTO userDTO) {
        User existingUser = getCurrentLoggedInUser();
        log.info("Inside update user");

        if (userDTO.getEmail() != null) existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getFirstName() != null) existingUser.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) existingUser.setLastName(userDTO.getLastName());
        if (userDTO.getPhoneNumber() != null) existingUser.setPhoneNumber(userDTO.getPhoneNumber());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.save(existingUser);

        return Response.builder()
                .status(200)
                .message("user updated successfully")
                .build();
    }

    @Override
    public Response deleteOwnAccount() {
        User user = getCurrentLoggedInUser();
        userRepository.delete(user);

        return Response.builder()
                .status(200)
                .message("user deleted successfully")
                .build();
    }

    @Override
    public Response getMyBookingHistory() {

        User user = getCurrentLoggedInUser();

        List<Booking> bookingList = bookingRepository.findByUserId(user.getId());


        List<BookingDTO> bookingDTOList = modelMapper.map(bookingList, new TypeToken<List<BookingDTO>>(){}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .bookings(bookingDTOList)
                .build();

    }

    @Override
    public Response forgotPassword(String email) {
        // Check if the user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún usuario con ese correo electrónico."));

        // Generate random unique token
        String token = java.util.UUID.randomUUID().toString();

        // Save token and the expiration date (15 min)
        user.setResetPasswordToken(token);
        user.setTokenExpirationDate(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Send the email using the emailService
        emailService.sendResetPasswordEmail(user.getEmail(), token);

        return Response.builder()
                .status(200)
                .message("Se ha enviado un enlace de recuperación a tu correo electrónico.")
                .build();
    }

    @Override
    public Response resetPassword(String token, String newPassword) {
        // Search the user with their token
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new NotFoundException("El enlace de recuperación es inválido o no existe."));

        // Verify if the token is expired
        if (user.getTokenExpirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialException("El enlace de recuperación ha expirado.");
        }

        // Encrypt the new password and save it
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clean the token from de bd so we can't use it again
        user.setResetPasswordToken(null);
        user.setTokenExpirationDate(null);
        userRepository.save(user);

        return Response.builder()
                .status(200)
                .message("Contraseña actualizada correctamente.")
                .build();
    }
}
