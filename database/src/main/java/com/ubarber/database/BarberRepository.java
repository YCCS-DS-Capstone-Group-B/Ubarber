package com.ubarber.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarberRepository extends JpaRepository<Barber, Long> {
    // define methods to retrieve and manipulate data in the Barber table
    Optional<Barber> findById(Long aLong);
    List<Barber> findByGeoHashStartingWith(String geoHashPrefix);
   // Optional<Barber> findByName(Name name);
}
