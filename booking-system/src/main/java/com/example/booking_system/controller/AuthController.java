package com.example.booking_system.controller;

import com.example.booking_system.model.User;
import com.example.booking_system.security.JwtUtil;
import com.example.booking_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController( UserService userService , JwtUtil jwtUtil ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public User signup( @RequestBody User user ) {
        return userService.createUser( user );
    }

    @PostMapping("/login")
    public String login( @RequestBody LoginRequest request ) {

        User validatedUser = userService.validateLogin( request.getEmail() , request.getPassword() );
        String token = jwtUtil.generateToken( validatedUser.getEmail() , validatedUser.getRole().toString() );
        return token;
    }

    public static class LoginRequest {
        String email;
        String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public void setEmail( String email ) {
            this.email = email;
        }

        public void setPassword( String password ) {
            this.password = password;
        }
    }

}