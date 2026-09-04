package com.example.booking_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table( name = "shows" )
public class Show {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long id;

    
    private String name;

    private String venue;

    private LocalDateTime startTime;

    @JsonManagedReference
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<Seat> seats;

    public Show() {}

    //Getters

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    //Setters

    public void setId( long id ) {
        this.id = id;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setVenue( String venue) {
        this.venue = venue;
    }

    public void setStartTime( LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setSeats( List<Seat> seats ) {
        this.seats = seats;
    }

}