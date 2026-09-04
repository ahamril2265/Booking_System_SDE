package com.example.booking_system.service;

import com.example.booking_system.model.User;
import com.example.booking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.booking_system.exception.ResourceNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService ( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    public User createUser( User user ) {
        return userRepository.save( user );
    }

    public User getUserById( Long userId ) {
        User user = userRepository.findById( userId ).orElseThrow( () -> new ResourceNotFoundException("User Not Found"));
        return user;
    }

    public User validateLogin( String email, String password ) {
        User user = userRepository.findByEmail( email ).orElseThrow( () -> new ResourceNotFoundException("Invalid Credentials"));
        if (!user.getPassword().equals(password)) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        return user;
    }

}