package com.claud.HotelBooking.controllersTets;

import com.claud.HotelBooking.controllers.UserController;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.UserDTO;
import com.claud.HotelBooking.security.CustomUserDetailsService;
import com.claud.HotelBooking.security.JwtUtils;
import com.claud.HotelBooking.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Necesarios porque AuthFilter se carga en el contexto
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void getAllUsers_shouldReturnOk() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(new Response());

        mockMvc.perform(get("/api/users/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void updateOwnAccount_shouldReturnOk() throws Exception {

        when(userService.updateOwnAccount(any(UserDTO.class)))
                .thenReturn(new Response());

        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "email": "john@example.com",
                                    "phoneNumber": "123456789"
                                }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void deleteOwnAccount_shouldReturnOk() throws Exception {

        when(userService.deleteOwnAccount())
                .thenReturn(new Response());

        mockMvc.perform(delete("/api/users/delete")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void getOwnAccountDetails_shouldReturnOk() throws Exception {

        when(userService.getOwnAccountDetails())
                .thenReturn(new Response());

        mockMvc.perform(get("/api/users/account")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void getMyBookingHistory_shouldReturnOk() throws Exception {

        when(userService.getMyBookingHistory())
                .thenReturn(new Response());

        mockMvc.perform(get("/api/users/bookings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}