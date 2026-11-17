package com.example.booking_system.service;

import com.example.booking_system.model.Booking;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Seat;
import com.example.booking_system.model.Show;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.BookingRepository;
import com.example.booking_system.model.SeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.booking_system.exception.ResourceNotFoundException;
import com.example.booking_system.exception.SeatUnavailableException;
import com.example.booking_system.exception.BookingNotPayableException;
import java.util.Optional;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;

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
    public Booking createBooking( Long seatId, User user, Show show , String idempotencyKey ) {

        Optional<Booking> existingBooking = bookingRepository.findByIdempotencyKey(idempotencyKey);
        if (existingBooking.isPresent()) {
            return existingBooking.get();
        }
        
        Seat seat = seatService.lockSeat( seatId );

        Booking booking = new Booking();
        booking.setStatus( BookingStatus.PENDING);
        booking.setExpiry( LocalDateTime.now().plusMinutes(5));
        booking.setUser( user );
        booking.setShow( show );
        booking.setSeat( seat );
        booking.setIdempotencyKey(idempotencyKey);

        return bookingRepository.save(booking);

    }

    @Transactional
    public Booking processPayment( Long bookingId ) {

        // Row lock serialises payment with the expiry job, so the status read below can't go stale.
        Booking booking = bookingRepository.findByIdForUpdate( bookingId ).orElseThrow( () -> new ResourceNotFoundException("Booking ID Not Found."));

        if ( booking.getStatus() != BookingStatus.PENDING ) {
            throw new BookingNotPayableException("Booking is " + booking.getStatus() + " and cannot be paid.");
        }
        if ( booking.getExpireTime() != null && booking.getExpireTime().isBefore( LocalDateTime.now() ) ) {
            throw new BookingNotPayableException("Booking hold has expired and cannot be paid.");
        }

        boolean success = Math.random() > 0.3;

        if ( success ) {
            booking.setStatus( BookingStatus.CONFIRMED );
            return bookingRepository.save(booking);
        } else {
            Seat seat = booking.getSeat();
            if (seat != null) {
                seat.setSeatStatus(SeatStatus.AVAILABLE);
            }
            booking.setStatus( BookingStatus.CANCELLED );
            return bookingRepository.save(booking);
        }
    }

    @Scheduled( fixedRate = 60000)
    @Transactional
    public void expireStaleBookings() {

        List<Booking> expiredBookings = bookingRepository.findByStatusAndLockExpiresAtBefore( BookingStatus.PENDING , LocalDateTime.now() );

        for (Booking expiredBooking : expiredBookings) {
            expiredBooking.setStatus(BookingStatus.EXPIRED);
            
            Seat seat = expiredBooking.getSeat();
            if (seat != null) {
                seat.setSeatStatus(SeatStatus.AVAILABLE);
            }
            
            bookingRepository.save(expiredBooking);
        }

    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

}