package com.ubarber.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    Optional<AppointmentSlot> findById(Long aLong);
    List<AppointmentSlot> findByBarberId(Long barberId);
    List<AppointmentSlot> findByStartTime(String startTime);
    List<AppointmentSlot> findByEndTime(String endTime);

}
