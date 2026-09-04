package com.example.booking_system.controller;

import com.example.booking_system.model.Show;
import com.example.booking_system.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;

    @Autowired
    public ShowController( ShowService showService ) {
        this.showService = showService;
    }

    @PostMapping
    public Show createShow( @RequestBody Show show ) {
        return showService.createShow( show );
    }

}