package com.claud.HotelBooking.controllersTets;

import com.claud.HotelBooking.controllers.RoomController;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.RoomDTO;
import com.claud.HotelBooking.enums.RoomType;
import com.claud.HotelBooking.services.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoomControllerTest {

    private MockMvc mockMvc;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomService = mock(RoomService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new RoomController(roomService))
                .build();
    }

    @Test
    void getAllRooms_shouldReturnOk() throws Exception {

        Response response = Response.builder()
                .status(200)
                .message("Rooms retrieved successfully")
                .build();

        when(roomService.getAllRooms()).thenReturn(response);

        mockMvc.perform(get("/api/rooms/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Rooms retrieved successfully"));

        verify(roomService).getAllRooms();
    }

    @Test
    void getRoomById_shouldReturnOk() throws Exception {

        Response response = Response.builder()
                .status(200)
                .message("Room found")
                .room(RoomDTO.builder()
                        .id(1L)
                        .roomNumber(101)
                        .type(RoomType.STANDARD_ROOM)
                        .pricePerNight(new BigDecimal("100.00"))
                        .capacity(2)
                        .description("Standard room")
                        .build())
                .build();

        when(roomService.getRoomById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/rooms/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.room.id").value(1))
                .andExpect(jsonPath("$.room.roomNumber").value(101))
                .andExpect(jsonPath("$.room.type")
                        .value("Standard Room"));

        verify(roomService).getRoomById(1L);
    }

    @Test
    void getAllRoomTypes_shouldReturnOk() throws Exception {

        when(roomService.getAllRoomTypes())
                .thenReturn(List.of(
                        RoomType.STANDARD_ROOM,
                        RoomType.DELUXE_SUITE,
                        RoomType.MENDLS_SUITE,
                        RoomType.THE_GRAND_SUITE
                ));

        mockMvc.perform(get("/api/rooms/types")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]")
                        .value("Standard Room"))
                .andExpect(jsonPath("$[1]")
                        .value("Deluxe Suite"))
                .andExpect(jsonPath("$[2]")
                        .value("Mendl's Suite"))
                .andExpect(jsonPath("$[3]")
                        .value("The Grand Suite"));

        verify(roomService).getAllRoomTypes();
    }

    @Test
    void searchRoom_shouldReturnOk() throws Exception {

        Response response = Response.builder()
                .status(200)
                .message("Search completed")
                .build();

        when(roomService.searchRoom("101"))
                .thenReturn(response);

        mockMvc.perform(get("/api/rooms/search")
                        .param("input", "101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Search completed"));

        verify(roomService).searchRoom("101");
    }

    @Test
    void getAvailableRooms_withoutDates_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/rooms/available")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(roomService);
    }

    @Test
    void getAvailableRooms_shouldReturnOk() throws Exception {

        Response response = Response.builder()
                .status(200)
                .message("Available rooms")
                .build();

        when(roomService.getAvailableRooms(
                eq(LocalDate.of(2026, 6, 10)),
                eq(LocalDate.of(2026, 6, 15)),
                eq(RoomType.STANDARD_ROOM)
        )).thenReturn(response);

        mockMvc.perform(get("/api/rooms/available")
                        .param("checkInDate", "2026-06-10")
                        .param("checkOutDate", "2026-06-15")
                        .param("roomType", "standard_room")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Available rooms"));

        verify(roomService).getAvailableRooms(
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 15),
                RoomType.STANDARD_ROOM
        );
    }

    @Test
    void getAvailableRooms_withAllRooms_shouldPassNullType()
            throws Exception {

        Response response = Response.builder()
                .status(200)
                .message("All available rooms")
                .build();

        when(roomService.getAvailableRooms(
                any(LocalDate.class),
                any(LocalDate.class),
                isNull()
        )).thenReturn(response);

        mockMvc.perform(get("/api/rooms/available")
                        .param("checkInDate", "2026-06-10")
                        .param("checkOutDate", "2026-06-15")
                        .param("roomType", "all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(roomService).getAvailableRooms(
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 15),
                null
        );
    }

    @Test
    void getAvailableRooms_withInvalidType_shouldPassNullType()
            throws Exception {

        Response response = Response.builder()
                .status(200)
                .message("Available rooms")
                .build();

        when(roomService.getAvailableRooms(
                any(LocalDate.class),
                any(LocalDate.class),
                isNull()
        )).thenReturn(response);

        mockMvc.perform(get("/api/rooms/available")
                        .param("checkInDate", "2026-06-10")
                        .param("checkOutDate", "2026-06-15")
                        .param("roomType", "invalid-type")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(roomService).getAvailableRooms(
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 15),
                null
        );
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void deleteRoom_asAdmin_shouldReturnOk() throws Exception {

        Response response = Response.builder()
                .status(200)
                .message("Room deleted")
                .build();

        when(roomService.deleteRoom(1L))
                .thenReturn(response);

        mockMvc.perform(delete("/api/rooms/delete/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Room deleted"));

        verify(roomService).deleteRoom(1L);
    }
}
