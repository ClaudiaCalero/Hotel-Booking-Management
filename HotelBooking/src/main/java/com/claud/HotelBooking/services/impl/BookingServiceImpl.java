package com.claud.HotelBooking.services.impl;

import com.claud.HotelBooking.dtos.BookingDTO;
import com.claud.HotelBooking.dtos.Response;
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
        return null;
    }

    @Override
    public Response findBookingByReferenceNo(String bookingReference) {
        return null;
    }

    @Override
    public Response updateBooking(BookingDTO bookingDTO) {
        return null;
    }
}
