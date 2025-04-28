package com.claud.HotelBooking.services;

import com.claud.HotelBooking.dtos.LoginRequest;
import com.claud.HotelBooking.dtos.RegistrationRequest;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.UserDTO;

public interface UserService {

    Response registerUser(RegistrationRequest registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    Response getOwnAccountDetails();
    Response getCurrentLoggedInUser();
    Response updateOwnAccount(UserDTO userDTO);
    Response deleteOwnAccount();
    Response getMyBookingHistory();
}
