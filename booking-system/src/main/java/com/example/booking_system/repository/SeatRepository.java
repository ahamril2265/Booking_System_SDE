package com.example.booking_system.repository;

import com.example.booking_system.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface SeatRepository extends JpaRepository< Seat , Long > {
    @Lock( LockModeType.PESSIMISTIC_WRITE )
    @Query( "SELECT s FROM Seat s WHERE s.id = :id" )
    Optional<Seat> findByIdForUpdate( @Param("id") Long id );
}