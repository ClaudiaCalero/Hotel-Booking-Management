package com.claud.HotelBooking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({
            "/",
            "/home",
            "/login",
            "/register",
            "/forgot-password",
            "/reset-password",
            "/rooms",
            "/find-booking",
            "/profile",
            "/edit-profile",
            "/room-details/{roomId}",
            "/payment/{bookingReference}/{amount}",
            "/payment-success/{bookingReference}",
            "/payment-failed/{bookingReference}",
            "/admin",
            "/admin/manage-rooms",
            "/admin/add-room",
            "/admin/edit-room/{roomId}",
            "/admin-register",
            "/admin/all-rooms",
            "/admin/manage-bookings",
            "/admin/edit-booking/{bookingReference}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}