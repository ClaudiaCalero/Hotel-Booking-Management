package com.claud.HotelBooking;

import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.RoomDTO;
import com.claud.HotelBooking.entities.Room;
import com.claud.HotelBooking.enums.RoomType;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.RoomRepository;
import com.claud.HotelBooking.services.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;
    private RoomDTO roomDTO;

    @BeforeEach
    void setUp() {

        room = Room.builder()
                .id(1L)
                .roomNumber(101)
                .type(RoomType.STANDARD_ROOM)
                .pricePerNight(new BigDecimal("100.00"))
                .capacity(2)
                .description("Comfortable single room")
                .imageUrls(List.of("/images/room1.jpg"))
                .build();

        roomDTO = RoomDTO.builder()
                .id(1L)
                .roomNumber(101)
                .type(RoomType.STANDARD_ROOM)
                .pricePerNight(new BigDecimal("100.00"))
                .capacity(2)
                .description("Comfortable single room")
                .imageUrls(List.of("/images/room1.jpg"))
                .build();
    }

    // =========================================================
    // ADD ROOM
    // =========================================================

    @Test
    void addRoom_shouldSuccessfullyAddRoomWithoutImages() {

        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Response response = roomService.addRoom(roomDTO, null);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Room successfully added", response.getMessage());

        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void addRoom_shouldSuccessfullyAddRoomWithEmptyImageList() {

        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Response response = roomService.addRoom(
                roomDTO,
                Collections.emptyList()
        );

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Room successfully added", response.getMessage());

        verify(roomRepository).save(any(Room.class));
    }

    // =========================================================
    // UPDATE ROOM
    // =========================================================

    @Test
    void updateRoom_shouldSuccessfullyUpdateRoom() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(roomRepository.save(any(Room.class)))
                .thenReturn(room);

        RoomDTO updateDTO = RoomDTO.builder()
                .id(1L)
                .roomNumber(102)
                .type(RoomType.DELUXE_SUITE)
                .pricePerNight(new BigDecimal("150.00"))
                .capacity(3)
                .description("Updated room")
                .build();

        Response response = roomService.updateRoom(updateDTO, null);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Room updated successfully", response.getMessage());

        assertEquals(102, room.getRoomNumber());
        assertEquals(RoomType.DELUXE_SUITE, room.getType());
        assertEquals(new BigDecimal("150.00"), room.getPricePerNight());
        assertEquals(3, room.getCapacity());
        assertEquals("Updated room", room.getDescription());

        verify(roomRepository).findById(1L);
        verify(roomRepository).save(room);
    }

    @Test
    void updateRoom_shouldThrowExceptionWhenRoomDoesNotExist() {

        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        RoomDTO updateDTO = RoomDTO.builder()
                .id(999L)
                .roomNumber(102)
                .build();

        assertThrows(
                NotFoundException.class,
                () -> roomService.updateRoom(updateDTO, null)
        );

        verify(roomRepository).findById(999L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void updateRoom_shouldIgnoreInvalidValues() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(roomRepository.save(any(Room.class)))
                .thenReturn(room);

        RoomDTO updateDTO = RoomDTO.builder()
                .id(1L)
                .roomNumber(-1)
                .pricePerNight(BigDecimal.valueOf(-50))
                .capacity(0)
                .description(null)
                .type(null)
                .build();

        Response response = roomService.updateRoom(updateDTO, null);

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        // Original values should remain unchanged
        assertEquals(101, room.getRoomNumber());
        assertEquals(new BigDecimal("100.00"), room.getPricePerNight());
        assertEquals(2, room.getCapacity());
        assertEquals(RoomType.STANDARD_ROOM, room.getType());
        assertEquals("Comfortable single room", room.getDescription());

        verify(roomRepository).save(room);
    }

    // =========================================================
    // GET ALL ROOMS
    // =========================================================

    @Test
    void getAllRooms_shouldReturnAllRooms() {

        Room room2 = Room.builder()
                .id(2L)
                .roomNumber(102)
                .type(RoomType.DELUXE_SUITE)
                .pricePerNight(new BigDecimal("150.00"))
                .capacity(3)
                .description("Deluxe Suite")
                .imageUrls(List.of("/images/room2.jpg"))
                .build();

        when(roomRepository.findAll(any(Sort.class)))
                .thenReturn(Arrays.asList(room2, room));

        Response response = roomService.getAllRooms();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());

        assertNotNull(response.getRooms());
        assertEquals(2, response.getRooms().size());

        assertEquals(102, response.getRooms().get(0).getRoomNumber());
        assertEquals(101, response.getRooms().get(1).getRoomNumber());

        verify(roomRepository).findAll(any(Sort.class));
    }

    @Test
    void getAllRooms_shouldReturnEmptyListWhenNoRoomsExist() {

        when(roomRepository.findAll(any(Sort.class)))
                .thenReturn(Collections.emptyList());

        Response response = roomService.getAllRooms();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());

        assertNotNull(response.getRooms());
        assertTrue(response.getRooms().isEmpty());

        verify(roomRepository).findAll(any(Sort.class));
    }

    // =========================================================
    // GET ROOM BY ID
    // =========================================================

    @Test
    void getRoomById_shouldReturnRoom() {

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(modelMapper.map(room, RoomDTO.class))
                .thenReturn(roomDTO);

        Response response = roomService.getRoomById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());

        assertNotNull(response.getRoom());
        assertEquals(101, response.getRoom().getRoomNumber());

        verify(roomRepository).findById(1L);
        verify(modelMapper).map(room, RoomDTO.class);
    }

    @Test
    void getRoomById_shouldThrowExceptionWhenRoomDoesNotExist() {

        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> roomService.getRoomById(999L)
        );

        verify(roomRepository).findById(999L);
        verify(modelMapper, never()).map(any(), any());
    }

    // =========================================================
    // DELETE ROOM
    // =========================================================

    @Test
    void deleteRoom_shouldSuccessfullyDeleteRoom() {

        when(roomRepository.existsById(1L))
                .thenReturn(true);

        Response response = roomService.deleteRoom(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Room Deleted Successfully", response.getMessage());

        verify(roomRepository).existsById(1L);
        verify(roomRepository).deleteById(1L);
    }

    @Test
    void deleteRoom_shouldThrowExceptionWhenRoomDoesNotExist() {

        when(roomRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                NotFoundException.class,
                () -> roomService.deleteRoom(999L)
        );

        verify(roomRepository).existsById(999L);
        verify(roomRepository, never()).deleteById(anyLong());
    }

    // =========================================================
    // AVAILABLE ROOMS
    // =========================================================

    @Test
    void getAvailableRooms_shouldReturnAvailableRooms() {

        LocalDate checkIn = LocalDate.of(2026, 10, 10);
        LocalDate checkOut = LocalDate.of(2026, 10, 15);

        when(roomRepository.findAvailableRooms(
                checkIn,
                checkOut,
                RoomType.STANDARD_ROOM
        )).thenReturn(List.of(room));

        Response response = roomService.getAvailableRooms(
                checkIn,
                checkOut,
                RoomType.STANDARD_ROOM
        );

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());

        assertNotNull(response.getRooms());
        assertEquals(1, response.getRooms().size());

        assertEquals(101, response.getRooms().get(0).getRoomNumber());

        verify(roomRepository).findAvailableRooms(
                checkIn,
                checkOut,
                RoomType.STANDARD_ROOM
        );
    }

    @Test
    void getAvailableRooms_shouldReturnEmptyListWhenNoRoomsAreAvailable() {

        LocalDate checkIn = LocalDate.of(2026, 10, 10);
        LocalDate checkOut = LocalDate.of(2026, 10, 15);

        when(roomRepository.findAvailableRooms(
                checkIn,
                checkOut,
                RoomType.STANDARD_ROOM
        )).thenReturn(Collections.emptyList());

        Response response = roomService.getAvailableRooms(
                checkIn,
                checkOut,
                RoomType.STANDARD_ROOM
        );

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Success", response.getMessage());

        assertNotNull(response.getRooms());
        assertTrue(response.getRooms().isEmpty());
    }

    @Test
    void getAvailableRooms_shouldReturnErrorResponseWhenRepositoryFails() {

        LocalDate checkIn = LocalDate.of(2026, 10, 10);
        LocalDate checkOut = LocalDate.of(2026, 10, 15);

        when(roomRepository.findAvailableRooms(
                checkIn,
                checkOut,
                RoomType.STANDARD_ROOM
        )).thenThrow(new RuntimeException("Database error"));

        Response response = roomService.getAvailableRooms(
                checkIn,
                checkOut,
                RoomType.STANDARD_ROOM
        );

        assertNotNull(response);
        assertEquals(500, response.getStatus());
        assertTrue(
                response.getMessage()
                        .contains("Error searching for available rooms")
        );
    }

    // =========================================================
    // GET ALL ROOM TYPES
    // =========================================================

    @Test
    void getAllRoomTypes_shouldReturnAllRoomTypes() {

        List<RoomType> result = roomService.getAllRoomTypes();

        assertNotNull(result);
        assertEquals(
                RoomType.values().length,
                result.size()
        );

        assertEquals(
                Arrays.asList(RoomType.values()),
                result
        );
    }

    // =========================================================
    // SEARCH ROOM
    // =========================================================
    @Test
    void searchRoom_shouldReturnMatchingRooms() {

        List<Room> rooms = List.of(room);
        List<RoomDTO> roomDTOs = List.of(roomDTO);

        when(roomRepository.searchRooms("single"))
                .thenReturn(rooms);

        when(modelMapper.map(
                eq(rooms),
                org.mockito.ArgumentMatchers.<java.lang.reflect.Type>any()
        )).thenReturn(roomDTOs);

        Response response = roomService.searchRoom("single");

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());

        assertNotNull(response.getRooms());
        assertEquals(1, response.getRooms().size());

        assertEquals(101, response.getRooms().get(0).getRoomNumber());

        verify(roomRepository).searchRooms("single");

        verify(modelMapper).map(
                eq(rooms),
                org.mockito.ArgumentMatchers.<java.lang.reflect.Type>any()
        );
    }


    @Test
    void searchRoom_shouldReturnEmptyListWhenNoRoomsMatch() {

        List<Room> rooms = Collections.emptyList();
        List<RoomDTO> roomDTOs = Collections.emptyList();

        when(roomRepository.searchRooms("nonexistent"))
                .thenReturn(rooms);

        when(modelMapper.map(
                eq(rooms),
                org.mockito.ArgumentMatchers.<java.lang.reflect.Type>any()
        )).thenReturn(roomDTOs);

        Response response = roomService.searchRoom("nonexistent");

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());

        assertNotNull(response.getRooms());
        assertTrue(response.getRooms().isEmpty());

        verify(roomRepository).searchRooms("nonexistent");

        verify(modelMapper).map(
                eq(rooms),
                org.mockito.ArgumentMatchers.<java.lang.reflect.Type>any()
        );
    }



    // =========================================================
    // IMAGE VALIDATION
    // =========================================================

    @Test
    void addRoom_shouldRejectNonImageFile() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "This is not an image".getBytes()
        );

        List<MultipartFile> files = List.of(file);

        assertThrows(
                IllegalArgumentException.class,
                () -> roomService.addRoom(roomDTO, files)
        );

        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void addRoom_shouldIgnoreEmptyImageFiles() {

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        when(roomRepository.save(any(Room.class)))
                .thenReturn(room);

        Response response = roomService.addRoom(
                roomDTO,
                List.of(emptyFile)
        );

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Room successfully added", response.getMessage());

        verify(roomRepository).save(any(Room.class));
    }
}