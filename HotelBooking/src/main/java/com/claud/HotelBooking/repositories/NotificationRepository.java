package com.claud.HotelBooking.repositories;

import com.claud.HotelBooking.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
