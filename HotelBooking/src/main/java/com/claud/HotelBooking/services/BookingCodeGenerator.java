package com.claud.HotelBooking.services;

import com.claud.HotelBooking.entities.Booking;
import com.claud.HotelBooking.entities.BookingReference;
import com.claud.HotelBooking.repositories.BookingReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class BookingCodeGenerator {

    private final BookingReferenceRepository bookingReferenceRepository;

    public String generateBookingReference(){
        String bookingReference;
        //keep generating until a unique code is found
        do{
            bookingReference = generateRandomAlphaNumericCode(10); //generate code of length 10
        }while(isBookingReferenceExist(bookingReference)); //check if the code already exist, if not =exit

        saveBookingReferenceToDatabase(bookingReference); //save code to db
        return bookingReference;
    }

    private String generateRandomAlphaNumericCode(int length){
        String characters = "ABCDEFGHJKLMNÑOPQRSTUVWXYZ123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++){
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    private boolean isBookingReferenceExist(String bookingReference) {
        return bookingReferenceRepository.findByReferenceNum(bookingReference).isPresent();
    }

    private void saveBookingReferenceToDatabase(String bookingReference){
        BookingReference newBookingReference = BookingReference.builder().referenceNum(bookingReference).build();
        bookingReferenceRepository.save(newBookingReference);
    }
}
