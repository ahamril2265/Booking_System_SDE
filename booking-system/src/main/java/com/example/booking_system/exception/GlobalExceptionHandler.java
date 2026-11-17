package com.example.booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( ResourceNotFoundException.class )
    public ResponseEntity<Map<String, Object>> handleNotFound( ResourceNotFoundException ex ) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp" , LocalDateTime.now() );
        errorBody.put("status" , 404 );
        errorBody.put("error" , ex.getMessage() );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }

    @ExceptionHandler( SeatUnavailableException.class )
    public ResponseEntity<Map<String, Object>> handleSeatUnavailable( SeatUnavailableException ex ) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp" , LocalDateTime.now() );
        errorBody.put("status" , 409 );
        errorBody.put("error" , ex.getMessage() );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

    @ExceptionHandler( BookingNotPayableException.class )
    public ResponseEntity<Map<String, Object>> handleBookingNotPayable( BookingNotPayableException ex ) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp" , LocalDateTime.now() );
        errorBody.put("status" , 409 );
        errorBody.put("error" , ex.getMessage() );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

}
