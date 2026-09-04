package com.example.booking_system.model;

import jakarta.persistence.*;

@Entity
@Table( name = "users")
public class User {

    @Id 
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long id;

    @Column( nullable = false, unique = true )
    private String email;

    private String password;

    @Enumerated( EnumType.STRING )
    @Column( nullable = false , columnDefinition = "VARCHAR(  20 ) CHECK ( role IN ( 'USER', 'ADMIN'))")
    private Role role;

    public User(){}

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    } 

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole( Role role ) {
        this.role = role;
    }


}