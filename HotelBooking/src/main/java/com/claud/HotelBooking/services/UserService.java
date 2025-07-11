package com.claud.HotelBooking.services;

import com.claud.HotelBooking.dtos.LoginRequest;
import com.claud.HotelBooking.dtos.RegistrationRequest;
import com.claud.HotelBooking.dtos.Response;
import com.claud.HotelBooking.dtos.UserDTO;
import com.claud.HotelBooking.entities.User;

public interface UserService {

    Response registerUser(RegistrationRequest registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUsers();
    Response getOwnAccountDetails();
    User getCurrentLoggedInUser();
    Response updateOwnAccount(UserDTO userDTO);
    Response deleteOwnAccount();
    Response getMyBookingHistory();
}
