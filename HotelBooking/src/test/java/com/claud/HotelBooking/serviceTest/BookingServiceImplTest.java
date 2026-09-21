package com.claud.HotelBooking.serviceTest;

import com.claud.HotelBooking.dtos.BookingDTO;
import com.claud.HotelBooking.dtos.NotificationDTO;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.entities.Booking;
import com.claud.HotelBooking.entities.Room;
import com.claud.HotelBooking.entities.User;
import com.claud.HotelBooking.enums.BookingStatus;
import com.claud.HotelBooking.enums.PaymentStatus;
import com.claud.HotelBooking.exceptions.InvalidBookingStateAndDateException;
import com.claud.HotelBooking.exceptions.NotFoundException;
import com.claud.HotelBooking.repositories.BookingRepository;
import com.claud.HotelBooking.repositories.RoomRepository;
import com.claud.HotelBooking.services.BookingCodeGenerator;
import com.claud.HotelBooking.services.NotificationService;
import com.claud.HotelBooking.services.UserService;
import com.claud.HotelBooking.services.impl.BookingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserService userService;

    @Mock
    private BookingCodeGenerator bookingCodeGenerator;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                roomRepository,
                notificationService,
                modelMapper,
                userService,
                bookingCodeGenerator
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ---------------------------------------------------------
    // getAllBookings
    // ---------------------------------------------------------

    @Test
    void getAllBookings_shouldReturnBookingsSuccessfully() {
        List<Booking> bookings = List.of(
                Booking.builder().id(2L).build(),
                Booking.builder().id(1L).build()
        );

        List<BookingDTO> bookingDTOs = List.of(
                BookingDTO.builder().id(2L).build(),
                BookingDTO.builder().id(1L).build()
        );

        when(bookingRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(bookings);

        when(modelMapper.map(
                eq(bookings),
                org.mockito.ArgumentMatchers.<java.lang.reflect.Type>any()
        )).thenReturn(bookingDTOs);

        Response response = bookingService.getAllBookings();

        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertEquals(bookingDTOs, response.getBookings());

        verify(bookingRepository).findAll(any(org.springframework.data.domain.Sort.class));
        verify(modelMapper).map(
                eq(bookings),
                org.mockito.ArgumentMatchers.<java.lang.reflect.Type>any()
        );
    }

    // ---------------------------------------------------------
    // createBooking - guest
    // ---------------------------------------------------------

    @Test
    void createBooking_asGuest_shouldCreateBookingSuccessfully() {
        SecurityContextHolder.clearContext();

        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(3);

        Room room = Room.builder()
                .id(10L)
                .roomNumber(101)
                .pricePerNight(new BigDecimal("100.00"))
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(10L)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("+34123456789")
                .build();

        when(roomRepository.findById(10L))
                .thenReturn(Optional.of(room));

        when(bookingRepository.isRoomAvailable(
                10L,
                checkIn,
                checkOut
        )).thenReturn(true);

        when(bookingCodeGenerator.generateBookingReference())
                .thenReturn("BOOK-123");

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    booking.setId(99L);
                    return booking;
                });

        Response response = bookingService.createBooking(bookingDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Booking is successful", response.getMessage());

        assertNotNull(response.getBooking());
        assertEquals(99L, response.getBooking().getId());
        assertEquals("BOOK-123", response.getBooking().getBookingReference());
        assertEquals(BookingStatus.BOOKED, response.getBooking().getBookingStatus());
        assertEquals(PaymentStatus.PENDING, response.getBooking().getPaymentStatus());
        assertEquals(new BigDecimal("300.00"), response.getBooking().getTotalPrice());
        assertEquals(10L, response.getBooking().getRoomId());

        assertEquals("John", response.getBooking().getGuestFirstName());
        assertEquals("Doe", response.getBooking().getGuestLastName());
        assertEquals("john@example.com", response.getBooking().getGuestEmail());
        assertEquals("+34123456789", response.getBooking().getGuestPhoneNumber());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());

        Booking savedBooking = captor.getValue();

        assertNull(savedBooking.getUser());
        assertEquals(room, savedBooking.getRoom());
        assertEquals(checkIn, savedBooking.getCheckInDate());
        assertEquals(checkOut, savedBooking.getCheckOutDate());
        assertEquals(new BigDecimal("300.00"), savedBooking.getTotalPrice());
        assertEquals("BOOK-123", savedBooking.getBookingReference());
        assertEquals(BookingStatus.BOOKED, savedBooking.getBookingStatus());
        assertEquals(PaymentStatus.PENDING, savedBooking.getPaymentStatus());

        verify(notificationService).sendEmail(any(NotificationDTO.class));
        verify(notificationService).sendSms(any(NotificationDTO.class));
        verify(notificationService).sendWhatsapp(any(NotificationDTO.class));
    }

    // ---------------------------------------------------------
    // createBooking - logged user
    // ---------------------------------------------------------

    @Test
    void createBooking_asLoggedUser_shouldUseCurrentUser() {
        User user = User.builder()
                .id(5L)
                .email("user@example.com")
                .phoneNumber("+34999999999")
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "user@example.com",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getCurrentLoggedInUser())
                .thenReturn(user);

        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);

        Room room = Room.builder()
                .id(20L)
                .pricePerNight(new BigDecimal("75.00"))
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(20L)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        when(roomRepository.findById(20L))
                .thenReturn(Optional.of(room));

        when(bookingRepository.isRoomAvailable(
                20L,
                checkIn,
                checkOut
        )).thenReturn(true);

        when(bookingCodeGenerator.generateBookingReference())
                .thenReturn("BOOK-USER");

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    booking.setId(50L);
                    return booking;
                });

        Response response = bookingService.createBooking(bookingDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Booking is successful", response.getMessage());
        assertEquals(50L, response.getBooking().getId());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());

        Booking savedBooking = captor.getValue();

        assertEquals(user, savedBooking.getUser());
        assertEquals(room, savedBooking.getRoom());

        // Guest data should not be populated for logged users
        assertNull(savedBooking.getGuestFirstName());
        assertNull(savedBooking.getGuestLastName());
        assertNull(savedBooking.getGuestEmail());
        assertNull(savedBooking.getGuestPhoneNumber());

        verify(userService).getCurrentLoggedInUser();
    }

    // ---------------------------------------------------------
    // createBooking validation
    // ---------------------------------------------------------

    @Test
    void createBooking_guestWithoutFirstName_shouldThrowException() {
        SecurityContextHolder.clearContext();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("123")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .build();

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals("Guest first name is required", exception.getMessage());

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_guestWithoutLastName_shouldThrowException() {
        SecurityContextHolder.clearContext();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("John")
                .guestLastName(" ")
                .guestEmail("john@example.com")
                .guestPhoneNumber("123")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .build();

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals("Guest last name is required", exception.getMessage());

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_guestWithoutEmail_shouldThrowException() {
        SecurityContextHolder.clearContext();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("")
                .guestPhoneNumber("123")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .build();

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals("Guest email is required", exception.getMessage());

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_guestWithoutPhone_shouldThrowException() {
        SecurityContextHolder.clearContext();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .build();

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals("Guest phone number is required", exception.getMessage());

        verifyNoInteractions(roomRepository);
    }

    @Test
    void createBooking_roomNotFound_shouldThrowException() {
        SecurityContextHolder.clearContext();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(999L)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("123")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .build();

        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException exception =
                assertThrows(
                        NotFoundException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals("Room Not Found", exception.getMessage());
    }

    @Test
    void createBooking_checkInBeforeToday_shouldThrowException() {
        SecurityContextHolder.clearContext();

        LocalDate checkIn = LocalDate.now().minusDays(1);

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("123")
                .checkInDate(checkIn)
                .checkOutDate(checkIn.plusDays(1))
                .build();

        Room room = Room.builder()
                .id(1L)
                .pricePerNight(new BigDecimal("100.00"))
                .build();

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals(
                "Check-in date cannot be before today",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .isRoomAvailable(anyLong(), any(), any());
    }

    @Test
    void createBooking_checkOutBeforeCheckIn_shouldThrowException() {
        SecurityContextHolder.clearContext();

        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = checkIn.minusDays(1);

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("123")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        Room room = Room.builder()
                .id(1L)
                .pricePerNight(new BigDecimal("100.00"))
                .build();

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals(
                "Check-out date cannot be before check-in date",
                exception.getMessage()
        );
    }

    @Test
    void createBooking_sameCheckInAndCheckOut_shouldThrowException() {
        SecurityContextHolder.clearContext();

        LocalDate date = LocalDate.now();

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("123")
                .checkInDate(date)
                .checkOutDate(date)
                .build();

        Room room = Room.builder()
                .id(1L)
                .pricePerNight(new BigDecimal("100.00"))
                .build();

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals(
                "Check-in date cannot be equal to check out date",
                exception.getMessage()
        );
    }

    @Test
    void createBooking_roomUnavailable_shouldThrowException() {
        SecurityContextHolder.clearContext();

        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);

        BookingDTO bookingDTO = BookingDTO.builder()
                .roomId(1L)
                .guestFirstName("John")
                .guestLastName("Doe")
                .guestEmail("john@example.com")
                .guestPhoneNumber("123")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        Room room = Room.builder()
                .id(1L)
                .pricePerNight(new BigDecimal("100.00"))
                .build();

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository.isRoomAvailable(
                1L,
                checkIn,
                checkOut
        )).thenReturn(false);

        InvalidBookingStateAndDateException exception =
                assertThrows(
                        InvalidBookingStateAndDateException.class,
                        () -> bookingService.createBooking(bookingDTO)
                );

        assertEquals(
                "Room is not available for the selected date ranges",
                exception.getMessage()
        );

        verify(bookingRepository, never()).save(any());
    }

    // ---------------------------------------------------------
    // findBookingByReferenceNum
    // ---------------------------------------------------------

    @Test
    void findBookingByReferenceNum_shouldReturnBooking() {
        Booking booking = Booking.builder()
                .id(10L)
                .bookingReference("BOOK-123")
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(10L)
                .bookingReference("BOOK-123")
                .build();

        when(bookingRepository.findByBookingReference("BOOK-123"))
                .thenReturn(Optional.of(booking));

        when(modelMapper.map(booking, BookingDTO.class))
                .thenReturn(bookingDTO);

        Response response =
                bookingService.findBookingByReferenceNum("BOOK-123");

        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertEquals(bookingDTO, response.getBooking());

        verify(bookingRepository)
                .findByBookingReference("BOOK-123");

        verify(modelMapper)
                .map(booking, BookingDTO.class);
    }

    @Test
    void findBookingByReferenceNum_notFound_shouldThrowException() {
        when(bookingRepository.findByBookingReference("INVALID"))
                .thenReturn(Optional.empty());

        NotFoundException exception =
                assertThrows(
                        NotFoundException.class,
                        () -> bookingService.findBookingByReferenceNum("INVALID")
                );

        assertEquals(
                "Booking with reference No: INVALID Not found",
                exception.getMessage()
        );

        verify(modelMapper, never())
                .map(any(Booking.class), eq(BookingDTO.class));
    }

    // ---------------------------------------------------------
    // updateBooking
    // ---------------------------------------------------------

    @Test
    void updateBooking_shouldUpdateStatusAndPaymentSuccessfully() {
        Booking existingBooking = Booking.builder()
                .id(10L)
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(10L)
                .bookingStatus(BookingStatus.CHECKED_IN)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(existingBooking));

        Response response = bookingService.updateBooking(bookingDTO);

        assertEquals(200, response.getStatus());
        assertEquals(
                "Booking Updated Successfully",
                response.getMessage()
        );

        assertEquals(
                BookingStatus.CHECKED_IN,
                existingBooking.getBookingStatus()
        );

        assertEquals(
                PaymentStatus.COMPLETED,
                existingBooking.getPaymentStatus()
        );

        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void updateBooking_onlyBookingStatus_shouldUpdateStatus() {
        Booking existingBooking = Booking.builder()
                .id(10L)
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(10L)
                .bookingStatus(BookingStatus.CANCELLED)
                .build();

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(existingBooking));

        bookingService.updateBooking(bookingDTO);

        assertEquals(
                BookingStatus.CANCELLED,
                existingBooking.getBookingStatus()
        );

        assertEquals(
                PaymentStatus.PENDING,
                existingBooking.getPaymentStatus()
        );

        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void updateBooking_onlyPaymentStatus_shouldUpdatePayment() {
        Booking existingBooking = Booking.builder()
                .id(10L)
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(10L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(existingBooking));

        bookingService.updateBooking(bookingDTO);

        assertEquals(
                BookingStatus.BOOKED,
                existingBooking.getBookingStatus()
        );

        assertEquals(
                PaymentStatus.COMPLETED,
                existingBooking.getPaymentStatus()
        );

        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void updateBooking_nullId_shouldThrowException() {
        BookingDTO bookingDTO = BookingDTO.builder()
                .bookingStatus(BookingStatus.BOOKED)
                .build();

        NotFoundException exception =
                assertThrows(
                        NotFoundException.class,
                        () -> bookingService.updateBooking(bookingDTO)
                );

        assertEquals(
                "Booking id is required",
                exception.getMessage()
        );

        verify(bookingRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBooking_bookingNotFound_shouldThrowException() {
        BookingDTO bookingDTO = BookingDTO.builder()
                .id(999L)
                .bookingStatus(BookingStatus.BOOKED)
                .build();

        when(bookingRepository.findById(999L))
                .thenReturn(Optional.empty());

        NotFoundException exception =
                assertThrows(
                        NotFoundException.class,
                        () -> bookingService.updateBooking(bookingDTO)
                );

        assertEquals(
                "Booking Not Found",
                exception.getMessage()
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBooking_noStatusChanges_shouldStillSaveBooking() {
        Booking existingBooking = Booking.builder()
                .id(10L)
                .bookingStatus(BookingStatus.BOOKED)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(10L)
                .build();

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(existingBooking));

        Response response = bookingService.updateBooking(bookingDTO);

        assertEquals(200, response.getStatus());
        assertEquals(
                "Booking Updated Successfully",
                response.getMessage()
        );

        verify(bookingRepository).save(existingBooking);
    }
}
