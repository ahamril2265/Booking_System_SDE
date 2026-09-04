package com.example.booking_system.controller;

import com.example.booking_system.model.Booking;
import com.example.booking_system.model.Show;
import com.example.booking_system.model.User;
import com.example.booking_system.service.BookingService;
import com.example.booking_system.service.ShowService;
import com.example.booking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return bookingService.createBooking( request.getSeatId(), user, show , request.getIdempotencyKey() );
    }

    @PostMapping("/{id}/pay")
    public Booking processPayment( @PathVariable Long id ) {

        return bookingService.processPayment( id );

    }

    @GetMapping
    public List<Booking> getAllBooking() {
        return bookingService.getAllBookings();
    }

    public static class BookingRequest {

        Long seatId;
        Long userId;
        Long showId;
        String idempotencyKey;

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

        public String getIdempotencyKey(){
            return idempotencyKey;
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
        public void setIdempotencyKey( String idempotencyKey ) {
            this.idempotencyKey = idempotencyKey;
        }

    }

}