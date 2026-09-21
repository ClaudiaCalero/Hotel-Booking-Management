package com.claud.HotelBooking.controllersTets;

import com.claud.HotelBooking.controllers.BookingController;
import com.claud.HotelBooking.security.CustomUserDetailsService;
import com.claud.HotelBooking.security.JwtUtils;
import com.claud.HotelBooking.services.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    // Necesarios porque AuthFilter se carga en el contexto
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void getAllBookings_shouldReturnOk() throws Exception {

        mockMvc.perform(get("/api/bookings/all"))
                .andExpect(status().isOk());
    }


    @Test
    void createBooking_shouldReturnOk() throws Exception {

        mockMvc.perform(post("/api/bookings")
                        .contentType("application/json")
                        .content("""
                                {
                                    "roomId": 1,
                                    "checkInDate": "2025-05-01",
                                    "checkOutDate": "2025-05-03"
                                }
                                """))
                .andExpect(status().isOk());
    }


    @Test
    void findBookingByReferenceNo_shouldReturnOk() throws Exception {

        mockMvc.perform(get("/api/bookings/ABC123"))
                .andExpect(status().isOk());
    }


    @Test
    void updateBooking_shouldReturnOk() throws Exception {

        mockMvc.perform(put("/api/bookings/update")
                        .contentType("application/json")
                        .content("""
                                {
                                    "id": 1,
                                    "roomId": 1,
                                    "checkInDate": "2025-05-01",
                                    "checkOutDate": "2025-05-03"
                                }
                                """))
                .andExpect(status().isOk());
    }
}