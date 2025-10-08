package com.claud.HotelBooking.repositories;

import com.claud.HotelBooking.entities.BookingReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BookingReferenceRepository extends JpaRepository<BookingReference, Long> {

    Optional<BookingReference> findByReferenceNum(String referenceNum);
}


