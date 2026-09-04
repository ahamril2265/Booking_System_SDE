package com.example.booking_system.controller;

import com.example.booking_system.model.Seat;
import com.example.booking_system.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seats")
public class SeatController {

    private final SeatService seatService;

    @Autowired
    public SeatController( SeatService seatService ) {
        this.seatService = seatService;
    }

    @PostMapping("/{id}/lock")
    public Seat lockSeat( @PathVariable Long id ) {
        return seatService.lockSeat( id );
    }

}