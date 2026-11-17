package com.example.booking_system.exception;

public class BookingNotPayableException extends RuntimeException {

    public BookingNotPayableException(String message) {
        super(message);
    }

}
