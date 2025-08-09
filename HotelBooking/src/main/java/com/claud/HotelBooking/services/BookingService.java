package com.claud.HotelBooking.services;

import com.claud.HotelBooking.dtos.BookingDTO;
import com.claud.HotelBooking.dtos.Response;

public interface BookingService {

    Response getAllBookings();
    Response createBooking(BookingDTO bookingDTO);
    Response findBookingByReferenceNum(String  bookingReference);
    Response updateBooking(BookingDTO bookingDTO);
}
