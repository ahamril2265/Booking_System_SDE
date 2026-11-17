package com.example.booking_system.repository;

import com.example.booking_system.model.Booking;
import com.example.booking_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;
import java.time.LocalDateTime;
import com.example.booking_system.model.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository< Booking, Long > {
    Optional<Booking> findByIdempotencyKey(String key);
    @Lock( LockModeType.PESSIMISTIC_WRITE )
    @Query( "SELECT b FROM Booking b WHERE b.id = :id" )
    Optional<Booking> findByIdForUpdate( @Param("id") Long id );

    @Lock( LockModeType.PESSIMISTIC_WRITE )
    List<Booking> findByStatusAndLockExpiresAtBefore(BookingStatus status, LocalDateTime time);
    List<Booking> findAll();
}