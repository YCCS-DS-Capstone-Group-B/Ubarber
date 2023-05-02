package com.ubarber.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Appointments {
    @Id
    private Long Id;
    @Column
    private Long barberId;
    @Column
    private Long clientId;
    @Column
    private Long appointmentSlotId;


    public Appointments() {

    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public Long getId() {
        return Id;
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

    public Appointments(Long Id, Long barberId, Long clientId, Long appointmentSlotId) {
        this.Id = Id;
        this.barberId = barberId;
        this.clientId = clientId;
        this.appointmentSlotId = appointmentSlotId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=" + Id +
                ", \"barberId\"=" + "\"" + barberId + "\"" +
                ", \"clientId\"=" + "\"" + clientId + "\"" +
                ", \"appointmentSlotId\"=" + "\"" + appointmentSlotId + "\"" +
                '}';
    }
}
