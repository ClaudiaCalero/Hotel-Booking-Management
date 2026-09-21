package com.claud.HotelBooking.controllersTets;

import com.claud.HotelBooking.controllers.AuthController;
import com.claud.HotelBooking.dtos.ForgotPasswordRequest;
import com.claud.HotelBooking.dtos.LoginRequest;
import com.claud.HotelBooking.dtos.RegistrationRequest;
import com.claud.HotelBooking.dtos.ResetPasswordRequest;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.enums.UserRole;
import com.claud.HotelBooking.security.CustomUserDetailsService;
import com.claud.HotelBooking.security.JwtUtils;
import com.claud.HotelBooking.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Response response;

    @BeforeEach
    void setUp() {
        response = Response.builder()
                .status(200)
                .message("Success")
                .build();
    }

    @Test
    void registerUser_shouldReturnOk() throws Exception {

        RegistrationRequest request = new RegistrationRequest(
                "John",
                "Doe",
                "john@example.com",
                "123456789",
                UserRole.CUSTOMER,
                "password123"
        );

        when(userService.registerUser(any(RegistrationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void loginUser_shouldReturnOk() throws Exception {

        LoginRequest request = new LoginRequest(
                "john@example.com",
                "password123"
        );

        when(userService.loginUser(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void forgotPassword_shouldReturnOk() throws Exception {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("john@example.com");

        when(userService.forgotPassword("john@example.com"))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void resetPassword_shouldReturnOk() throws Exception {

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("reset-token");
        request.setNewPassword("newPassword123");

        when(userService.resetPassword(
                "reset-token",
                "newPassword123"
        )).thenReturn(response);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void registerUser_withMissingFields_shouldReturnInternalServerError() throws Exception {

        RegistrationRequest request = new RegistrationRequest(
                "",
                "",
                "",
                "",
                UserRole.CUSTOMER,
                ""
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void loginUser_withMissingFields_shouldReturnInternalServerError() throws Exception {

        LoginRequest request = new LoginRequest(
                "",
                ""
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}