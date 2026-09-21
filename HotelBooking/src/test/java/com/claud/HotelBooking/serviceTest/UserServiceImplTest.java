package com.claud.HotelBooking.serviceTest;

import com.claud.HotelBooking.dtos.LoginRequest;
import com.claud.HotelBooking.dtos.RegistrationRequest;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.exceptions.InvalidCredentialException;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.UserRepository;
import com.claud.HotelBooking.security.JwtUtils;
import com.claud.HotelBooking.services.EmailService;
import com.claud.HotelBooking.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                userService,
                "adminEmail",
                "admin@test.com"
        );
    }

    // =========================================================
    // REGISTER USER
    // =========================================================

    @Test
    void registerUser_shouldCreateCustomerSuccessfully() {

        RegistrationRequest request = new RegistrationRequest(
                "Zero",
                "X",
                "zero@test.com",
                "123456789",
                null,
                "password123"
        );

        when(userRepository.existsByEmail("zero@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        Response response = userService.registerUser(request);

        assertEquals(200, response.getStatus());
        assertEquals("user created successfully", response.getMessage());

        verify(userRepository).existsByEmail("zero@test.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldRejectDuplicateEmail() {

        RegistrationRequest request = new RegistrationRequest(
                "Zero",
                "X",
                "zero@test.com",
                "123456789",
                null,
                "password123"
        );

        when(userRepository.existsByEmail("zero@test.com"))
                .thenReturn(true);

        assertThrows(
                InvalidCredentialException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void registerUser_shouldAssignAdminRoleToAdminEmail() {

        RegistrationRequest request = new RegistrationRequest(
                "Admin",
                "User",
                "admin@test.com",
                "123456789",
                null,
                "adminPassword"
        );

        when(userRepository.existsByEmail("admin@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("adminPassword"))
                .thenReturn("encodedPassword");

        userService.registerUser(request);

        verify(userRepository).save(argThat(user ->
                user.getRole() == UserRole.ADMIN
        ));
    }

    @Test
    void registerUser_shouldEncodePasswordBeforeSaving() {

        RegistrationRequest request = new RegistrationRequest(
                "Zero",
                "X",
                "zero@test.com",
                "123456789",
                null,
                "plainPassword"
        );

        when(userRepository.existsByEmail("zero@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("plainPassword"))
                .thenReturn("encodedPassword");

        userService.registerUser(request);

        verify(passwordEncoder).encode("plainPassword");

        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("encodedPassword")
        ));
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @Test
    void loginUser_shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest(
                "zero@test.com",
                "password123"
        );

        User user = User.builder()
                .id(1L)
                .email("zero@test.com")
                .password("encodedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();

        when(userRepository.findByEmail("zero@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtUtils.generateToken("zero@test.com"))
                .thenReturn("fake-jwt-token");

        Response response = userService.loginUser(request);

        assertEquals(200, response.getStatus());
        assertEquals(
                "user logged in successfully",
                response.getMessage()
        );
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(UserRole.CUSTOMER, response.getRole());
        assertTrue(response.getIsActive());

        verify(jwtUtils).generateToken("zero@test.com");
    }

    @Test
    void loginUser_shouldThrowExceptionWhenEmailDoesNotExist() {

        LoginRequest request = new LoginRequest(
                "unknown@test.com",
                "password123"
        );

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> userService.loginUser(request)
        );

        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    void loginUser_shouldThrowExceptionWhenPasswordIsIncorrect() {

        LoginRequest request = new LoginRequest(
                "zero@test.com",
                "wrongPassword"
        );

        User user = User.builder()
                .id(1L)
                .email("zero@test.com")
                .password("encodedPassword")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();

        when(userRepository.findByEmail("zero@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialException.class,
                () -> userService.loginUser(request)
        );

        verify(jwtUtils, never()).generateToken(anyString());
    }
}