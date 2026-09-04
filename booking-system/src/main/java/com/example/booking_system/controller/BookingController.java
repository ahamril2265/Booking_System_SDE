package com.example.booking_system.controller;

import com.example.booking_system.model.Booking;
import com.example.booking_system.model.Show;
import com.example.booking_system.model.User;
import com.example.booking_system.service.BookingService;
import com.example.booking_system.service.ShowService;
import com.example.booking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final ShowService showService;

    @Autowired
    public BookingController( BookingService bookingService , UserService userService , ShowService showService ) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.showService = showService;
    }

    @PostMapping
    public Booking createBooking( @RequestBody BookingRequest request) {

        User user = userService.getUserById( request.getUserId() );
        Show show = showService.getShowById( request.getShowId() );
        return bookingService.createBooking( request.getSeatId(), user, show );
    }
    public static class BookingRequest {

        Long seatId;
        Long userId;
        Long showId;

        //Getters
        public Long getSeatId() {
            return seatId;
        }

        public Long getUserId() {
            return userId;
        }

        public Long getShowId() {
            return showId;
        }

        //Setters
        public void setSeatId( Long seatId ){
            this.seatId = seatId;
        }
        public void setUserId( Long userId ) {
            this.userId = userId;
        }
        public void setShowId( Long showId ) {
            this.showId = showId;
        }

    }

}