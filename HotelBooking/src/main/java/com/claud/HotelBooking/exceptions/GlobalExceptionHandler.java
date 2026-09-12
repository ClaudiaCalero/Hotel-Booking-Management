package com.claud.HotelBooking.exceptions;

import com.claud.HotelBooking.dtos.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleAllUnknowExceptions(Exception ex) {
        Response response = Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException ex) {
        Response response = Response.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(NameValueRequiredException.class)
    public ResponseEntity<Response> handleNameValueRequiredException(NameValueRequiredException ex) {
        Response response = Response.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Response> handleInvalidCredentialException(InvalidCredentialException ex) {
        Response response = Response.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBookingStateAndDateException.class)
    public ResponseEntity<Response> handleInvalidBookingStateAndDateException(InvalidBookingStateAndDateException ex) {
        Response response = Response.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<com.claud.HotelBooking.dtos.Response> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        String realresponse = "Duplicate conflict in the database.";

        if (ex.getMostSpecificCause() != null) {
            realresponse = ex.getMostSpecificCause().getMessage();
        }

        com.claud.HotelBooking.dtos.Response response = com.claud.HotelBooking.dtos.Response.builder()
                .status(org.springframework.http.HttpStatus.CONFLICT.value())
                .message("Database Error: " + realresponse)
                .build();

        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.CONFLICT);
    }
}