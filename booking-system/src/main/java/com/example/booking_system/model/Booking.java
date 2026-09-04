package com.example.booking_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import javax.annotation.processing.Generated;

@Entity
@Table( name = "bookings" )
public class Booking {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long id;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime lockExpiresAt;

    @Column( unique = true )
    private String idempotencyKey;

    @ManyToOne
    private User user;

    @ManyToOne
    private Show show;

    @OneToOne
    private Seat seat;

    public Booking() {}

    //Getters
    public long getId() {
        return id;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getExpireTime() {
        return lockExpiresAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public User getUser() {
        return user;
    }

    public Show getShow() {
        return show;
    }

    public Seat getSeat() {
        return seat;
    }

    //Setters
    public void setId( long id ) {
        this.id = id;
    }

    public void setStatus( BookingStatus status ) {
        this.status = status;
    }

    public void setExpiry( LocalDateTime lockExpiresAt ) {
        this.lockExpiresAt = lockExpiresAt;
    }

    public void setIdempotencyKey( String idempotencyKey ) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    public void setShow( Show show ) {
        this.show = show;
    }

    public void setSeat( Seat seat ) {
        this.seat = seat;
    }

}