package com.example.booking_system.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table( name = "seats" )
public class Seat {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @Version
    private int version;

    @JsonBackReference
    @ManyToOne
    private Show show;

    public Seat() {}

    //Getters
    public long getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

    public int getVersion() {
        return version;
    }

    public Show getShow() {
        return show;
    }

    //Setters
    public void setId( long id ) {
        this.id = id;
    }

    public void setSeatNumber( String seatNumber ) {
        this.seatNumber = seatNumber;
    }

    public void setSeatStatus( SeatStatus seatStatus ) {
        this.seatStatus = seatStatus;
    }

    public void setVersion( int version ) {
        this.version = version;
    }

    public void setShow( Show show ) {
        this.show = show;
    }

}