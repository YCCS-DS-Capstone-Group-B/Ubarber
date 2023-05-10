package com.ubarber.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Appointment {
    @Id
    @Column
    private Long appointmentId;
    @Column
    private Long barberId;
    @Column
    private Long clientId;
    @Column
    private Long appointmentSlotId;


    public Appointment() {

    }

    public void setAppointmentId(Long id) {
        this.appointmentId = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getBarberId() {
        return barberId;
    }

    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getAppointmentSlotId() {
        return appointmentSlotId;
    }

    public void setAppointmentSlotId(Long appointmentSlotId) {
        this.appointmentSlotId = appointmentSlotId;
    }

    public Appointment(Long Id, Long barberId, Long clientId, Long appointmentSlotId) {
        this.appointmentId = Id;
        this.barberId = barberId;
        this.clientId = clientId;
        this.appointmentSlotId = appointmentSlotId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"appointmentId\"=" + appointmentId +
                ", \"barberId\"=" + "\"" + barberId + "\"" +
                ", \"clientId\"=" + "\"" + clientId + "\"" +
                ", \"appointmentSlotId\"=" + "\"" + appointmentSlotId + "\"" +
                '}';
    }
}
