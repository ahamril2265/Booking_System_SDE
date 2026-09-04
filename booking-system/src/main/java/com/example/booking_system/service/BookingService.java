package com.example.booking_system.service;

import com.example.booking_system.model.Booking;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Seat;
import com.example.booking_system.model.Show;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.booking_system.exception.ResourceNotFoundException;
import com.example.booking_system.exception.SeatUnavailableException;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatService seatService;

    @Autowired
    public BookingService( BookingRepository bookingRepository, SeatService seatService ) {
        this.bookingRepository = bookingRepository;
        this.seatService = seatService;
    }

    @Transactional
    public Booking createBooking( Long seatId, User user, Show show ) {
        
        Seat seat = seatService.lockSeat( seatId );

        Booking booking = new Booking();
        booking.setStatus( BookingStatus.PENDING);
        booking.setExpiry( LocalDateTime.now().plusMinutes(5));
        booking.setUser( user );
        booking.setShow( show );
        booking.setSeat( seat );

        return bookingRepository.save(booking);

    }

}