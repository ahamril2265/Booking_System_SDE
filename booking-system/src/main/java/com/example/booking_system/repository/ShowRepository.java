package com.example.booking_system.repository;

import com.example.booking_system.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends JpaRepository< Show, Long > {

}