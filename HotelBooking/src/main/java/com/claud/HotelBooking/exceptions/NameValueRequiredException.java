package com.claud.HotelBooking.exceptions;

public class NameValueRequiredException extends RuntimeException{

    public NameValueRequiredException(String message){
        super(message);
    }
}
