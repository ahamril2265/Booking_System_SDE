package com.example.booking_system.repository;

import com.example.booking_system.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository< Booking, Long > {
    Optional<Booking> findByIdempotencyKey(String key);
}