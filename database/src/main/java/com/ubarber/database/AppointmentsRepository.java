package com.ubarber.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentsRepository extends JpaRepository<Appointments, Long> {

    Optional<Appointments> findById(Long aLong);
    List<Appointments> findByBarberId(Long barberId);
    List<Appointments> findByClientId(Long clientId);
}
