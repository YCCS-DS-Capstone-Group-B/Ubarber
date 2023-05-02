package com.ubarber.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    // define methods to retrieve and manipulate data in the Client table
    Optional<Client> findById(Long aLong);
}

