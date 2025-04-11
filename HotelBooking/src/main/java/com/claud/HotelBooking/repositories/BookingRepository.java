package com.claud.HotelBooking.repositories;

import com.claud.HotelBooking.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Retrieves all bookings associated with a specific user ID.
    List<Booking> findByUserId(Long userId);

    // Finds a booking by its unique booking reference.
    Optional<Booking> findByBookingReference(String bookingReference);

    /**
     * Checks if a room is available for the given date range.
     * The room is considered available if:
     * - There are no bookings for that room where the requested date range overlaps
     *   with an existing booking's check-in and check-out dates.
     * - The booking status of any overlapping reservation is either 'BOOKED' or 'CHECKED_IN'.
     *
     * Returns true if no conflicting bookings are found, otherwise false.
     */
    @Query("""
       SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END
       FROM Booking b
       WHERE b.room.id = :roomId
         AND :checkInDate <= b.checkOutDate
         AND :checkOutDate >= b.checkInDate
         AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
       """)
    boolean isRoomAvailable(@Param("roomId") Long roomId,
                            @Param("checkInDate") LocalDate checkInDate,
                            @Param("checkOutDate") LocalDate checkOutDate);

}
