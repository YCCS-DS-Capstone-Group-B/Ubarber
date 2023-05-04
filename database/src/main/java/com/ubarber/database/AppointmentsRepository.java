package com.ubarber.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findById(Long aLong);
    List<Appointment> findByBarberId(Long barberId);
    List<Appointment> findByClientId(Long clientId);
}
