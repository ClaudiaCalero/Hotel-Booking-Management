package com.claud.HotelBooking.repositories;

import com.claud.HotelBooking.entities.Room;
import com.claud.HotelBooking.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    /**
     * Finds all available rooms for the given date range and optional room type.
     * A room is available if:
     * - It is NOT in the list of rooms already booked for overlapping dates.
     * - Its booking status is 'BOOKED' or 'CHECKED_IN'.
     * - The room type matches the given roomType parameter (if it's not null).
     */
    @Query("""
            SELECT r FROM Room r
            WHERE
                r.id NOT IN (
                    SELECT b.room.id
                    FROM Booking b
                    WHERE :checkInDate <= b.checkOutDate
                    AND :checkOutDate >= b.checkInDate
                    AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
                )
                AND (:roomType IS NULL OR r.type = :roomType)
            """)
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomType") RoomType roomType
    );
    /**
     * Searches for rooms based on a flexible text parameter.
     * It compares the parameter against:
     * - roomNumber (as string)
     * - room type (ignores case)
     * - price per night (as string)
     * - capacity (as string)
     * - description (ignores case and uses LIKE operator)
     */
    @Query("""
                SELECT r FROM Room r
                WHERE CAST(r.roomNumber AS string) LIKE %:searchParam%
                   OR LOWER(r.type) LIKE LOWER(:searchParam)
                   OR CAST(r.pricePerNight AS string) LIKE %:searchParam%
                   OR CAST(r.capacity AS string) LIKE %:searchParam%
                   OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchParam, '%'))
            """)
    List<Room> searchRooms(@Param("searchParam") String searchParam);

}
