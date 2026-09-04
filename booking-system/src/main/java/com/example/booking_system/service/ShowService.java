package com.example.booking_system.service;

import com.example.booking_system.model.Show;
import com.example.booking_system.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.booking_system.exception.ResourceNotFoundException;

@Service
public class ShowService {

    private final ShowRepository showRepository;

    @Autowired
    public ShowService( ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    public Show createShow( Show show ) {
        return showRepository.save( show );
    }

    public Show getShowById( Long showId ) {
        Show show = showRepository.findById( showId ).orElseThrow( () -> new ResourceNotFoundException("Show Not Found"));
        return show;
    }

}