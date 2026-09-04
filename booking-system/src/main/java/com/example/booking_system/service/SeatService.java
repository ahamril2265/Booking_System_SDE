package com.example.booking_system.service;

import com.example.booking_system.model.Seat;
import com.example.booking_system.model.SeatStatus;
import com.example.booking_system.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.booking_system.exception.ResourceNotFoundException;
import com.example.booking_system.exception.SeatUnavailableException;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService( SeatRepository seatRepository ) {
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Seat lockSeat( long seatId ) {

        Seat seat = seatRepository.findByIdForUpdate( seatId ).orElseThrow( () -> new ResourceNotFoundException("Seat Not Found"));

        if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
            throw new SeatUnavailableException("Seat is not available");
        }

        seat.setSeatStatus( SeatStatus.LOCKED );

        return seatRepository.save(seat);


    }

}