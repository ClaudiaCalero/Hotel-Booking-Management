package com.claud.HotelBooking.services.impl;

import com.claud.HotelBooking.dtos.BookingDTO;
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
import com.claud.HotelBooking.services.BookingService;
import com.claud.HotelBooking.services.NotificationService;
import com.claud.HotelBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final BookingCodeGenerator bookingCodeGenerator;

    @Override
    public Response getAllBookings() {
        return null;
    }

    @Override
    public Response createBooking(BookingDTO bookingDTO) {
        User currentUser = userService.getCurrentLoggedInUser();
        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(()-> new NotFoundException("Room Not Found"));
        //validate to ensure the checkin date is not before today
        if (bookingDTO.getCheckInDate().isBefore(LocalDate.now())){
            throw new InvalidBookingStateAndDateException("check-in date cannot be before today");
        }
        //validation to ensure the check-out date is not before check-in date
        if (bookingDTO.getCheckInDate().isBefore(bookingDTO.getCheckInDate())){
            throw new InvalidBookingStateAndDateException("check out date cannot be before check in date ");
        }
        //validation to ensure the check-in date is not same as check out date
        if (bookingDTO.getCheckInDate().isEqual(bookingDTO.getCheckOutDate())){
            throw new InvalidBookingStateAndDateException("check in date cannot be equal to check out date ");
        }
        
        //validate room availability
        boolean isAvailable = bookingRepository.isRoomAvailable(room.getId(), bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        if (!isAvailable) {
            throw new InvalidBookingStateAndDateException("Room is not available for the selected date ranges");
        }
        
        //calculate the total price needed to pay for the stay
        BigDecimal totalPrice = calculateTotalPrice(room, bookingDTO);
        String bookingReference = bookingCodeGenerator.generateBookingReference();

        //create and save booking
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setCheckInDate(booking.getCheckInDate());
        booking.setCheckOutDate(booking.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingReference(bookingReference);
        booking.setBookingStatus(BookingStatus.BOOKED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        //generate the payment url (will be sent via email)
        String payment = "http://localhost:3000/payment" + bookingReference + "/" + totalPrice;
        log.info("PAYMENT LINK: {}", paymentUrl);


    }


    @Override
    public Response findBookingByReferenceNo(String bookingReference) {
        return null;
    }

    @Override
    public Response updateBooking(BookingDTO bookingDTO) {
        return null;
    }

    private BigDecimal calculateTotalPrice(Room room, BookingDTO bookingDTO) {
        BigDecimal pricePerNight = room.getPricePerNight();
        long days = ChronoUNIT.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }


}
