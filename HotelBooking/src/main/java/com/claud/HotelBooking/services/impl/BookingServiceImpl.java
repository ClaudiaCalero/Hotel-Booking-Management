package com.claud.HotelBooking.services.impl;

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
import com.claud.HotelBooking.services.BookingService;
import com.claud.HotelBooking.services.NotificationService;
import com.claud.HotelBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        List<Booking> bookingList =
                bookingRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "id")
                );

        List<BookingDTO> bookingDTOList =
                modelMapper.map(
                        bookingList,
                        new TypeToken<List<BookingDTO>>() {}.getType()
                );

        return Response.builder()
                .status(200)
                .message("success")
                .bookings(bookingDTOList)
                .build();
    }

    @Override
    public Response createBooking(BookingDTO bookingDTO) {

        User currentUser = null;

        var authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {

            currentUser = userService.getCurrentLoggedInUser();
        }

        if (currentUser == null) {

            if (bookingDTO.getGuestFirstName() == null
                    || bookingDTO.getGuestFirstName().isBlank()) {

                throw new InvalidBookingStateAndDateException(
                        "Guest first name is required"
                );
            }

            if (bookingDTO.getGuestLastName() == null
                    || bookingDTO.getGuestLastName().isBlank()) {

                throw new InvalidBookingStateAndDateException(
                        "Guest last name is required"
                );
            }

            if (bookingDTO.getGuestEmail() == null
                    || bookingDTO.getGuestEmail().isBlank()) {

                throw new InvalidBookingStateAndDateException(
                        "Guest email is required"
                );
            }

            if (bookingDTO.getGuestPhoneNumber() == null
                    || bookingDTO.getGuestPhoneNumber().isBlank()) {

                throw new InvalidBookingStateAndDateException(
                        "Guest phone number is required"
                );
            }
        }

        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() ->
                        new NotFoundException("Room Not Found")
                );

        if (bookingDTO.getCheckInDate().isBefore(LocalDate.now())) {

            throw new InvalidBookingStateAndDateException(
                    "Check-in date cannot be before today"
            );
        }

        if (bookingDTO.getCheckOutDate()
                .isBefore(bookingDTO.getCheckInDate())) {

            throw new InvalidBookingStateAndDateException(
                    "Check-out date cannot be before check-in date"
            );
        }

        if (bookingDTO.getCheckOutDate()
                .isEqual(bookingDTO.getCheckInDate())) {

            throw new InvalidBookingStateAndDateException(
                    "Check-in date cannot be equal to check out date"
            );
        }

        boolean isAvailable =
                bookingRepository.isRoomAvailable(
                        room.getId(),
                        bookingDTO.getCheckInDate(),
                        bookingDTO.getCheckOutDate()
                );

        if (!isAvailable) {

            throw new InvalidBookingStateAndDateException(
                    "Room is not available for the selected date ranges"
            );
        }

        BigDecimal totalPrice =
                calculateTotalPrice(room, bookingDTO);

        String bookingReference =
                bookingCodeGenerator.generateBookingReference();

        Booking booking = new Booking();

        booking.setUser(currentUser);
        booking.setRoom(room);

        booking.setCheckInDate(
                bookingDTO.getCheckInDate()
        );

        booking.setCheckOutDate(
                bookingDTO.getCheckOutDate()
        );

        booking.setTotalPrice(totalPrice);
        booking.setBookingReference(bookingReference);

        booking.setBookingStatus(BookingStatus.BOOKED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        if (currentUser == null) {

            booking.setGuestFirstName(
                    bookingDTO.getGuestFirstName()
            );

            booking.setGuestLastName(
                    bookingDTO.getGuestLastName()
            );

            booking.setGuestEmail(
                    bookingDTO.getGuestEmail()
            );

            booking.setGuestPhoneNumber(
                    bookingDTO.getGuestPhoneNumber()
            );
        }

        bookingRepository.save(booking);

        String paymentUrl =
                "http://localhost:3000/payment/"
                        + bookingReference
                        + "/"
                        + totalPrice;

        log.info("PAYMENT LINK: {}", paymentUrl);

        String recipientEmail;
        String recipientPhone;

        if (currentUser != null) {

            recipientEmail = currentUser.getEmail();
            recipientPhone = currentUser.getPhoneNumber();

        } else {

            recipientEmail = booking.getGuestEmail();
            recipientPhone = booking.getGuestPhoneNumber();
        }

        NotificationDTO notificationDTO =
                NotificationDTO.builder()
                        .recipient(recipientEmail)
                        .phoneNumber(recipientPhone)
                        .subject("Booking Confirmation")
                        .body(
                                String.format(
                                        "Your booking has been created successfully."
                                                + "\n\n"
                                                + "Your booking reference is: %s"
                                                + "\n\n"
                                                + "Please proceed with your payment using the payment link below:"
                                                + "\n%s",
                                        bookingReference,
                                        paymentUrl
                                )
                        )
                        .bookingReference(bookingReference)
                        .build();

        notificationService.sendEmail(notificationDTO);
        notificationService.sendSms(notificationDTO);
        notificationService.sendWhatsapp(notificationDTO);

        BookingDTO savedBookingDTO =
                BookingDTO.builder()
                        .id(booking.getId())
                        .checkInDate(booking.getCheckInDate())
                        .checkOutDate(booking.getCheckOutDate())
                        .bookingReference(booking.getBookingReference())
                        .bookingStatus(booking.getBookingStatus())
                        .paymentStatus(booking.getPaymentStatus())
                        .totalPrice(booking.getTotalPrice())
                        .roomId(room.getId())
                        .guestFirstName(booking.getGuestFirstName())
                        .guestLastName(booking.getGuestLastName())
                        .guestEmail(booking.getGuestEmail())
                        .guestPhoneNumber(booking.getGuestPhoneNumber())
                        .build();

        return Response.builder()
                .status(200)
                .message("Booking is successful")
                .booking(savedBookingDTO)
                .build();
    }

    @Override
    public Response findBookingByReferenceNum(String bookingReference) {

        Booking booking =
                bookingRepository.findByBookingReference(bookingReference)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Booking with reference No: "
                                                + bookingReference
                                                + " Not found"
                                )
                        );

        BookingDTO bookingDTO =
                modelMapper.map(
                        booking,
                        BookingDTO.class
                );

        return Response.builder()
                .status(200)
                .message("success")
                .booking(bookingDTO)
                .build();
    }

    @Override
    public Response updateBooking(BookingDTO bookingDTO) {

        if (bookingDTO.getId() == null) {
            throw new NotFoundException("Booking id is required");
        }

        Booking existingBooking =
                bookingRepository.findById(bookingDTO.getId())
                        .orElseThrow(() ->
                                new NotFoundException("Booking Not Found")
                        );

        if (bookingDTO.getBookingStatus() != null) {

            existingBooking.setBookingStatus(
                    bookingDTO.getBookingStatus()
            );
        }

        if (bookingDTO.getPaymentStatus() != null) {

            existingBooking.setPaymentStatus(
                    bookingDTO.getPaymentStatus()
            );
        }

        bookingRepository.save(existingBooking);

        return Response.builder()
                .status(200)
                .message("Booking Updated Successfully")
                .build();
    }

    private BigDecimal calculateTotalPrice(
            Room room,
            BookingDTO bookingDTO) {

        BigDecimal pricePerNight =
                room.getPricePerNight();

        long days =
                ChronoUnit.DAYS.between(
                        bookingDTO.getCheckInDate(),
                        bookingDTO.getCheckOutDate()
                );

        return pricePerNight.multiply(
                BigDecimal.valueOf(days)
        );
    }
}

