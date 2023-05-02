package com.ubarber.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class AppointmentSlot {
    @Id
    private Long appointmentSlotId;
    @Column
    private Long barberId;
    @Column
    private String startTime;
    @Column
    private String endTime;
    @Column
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AppointmentSlot() {

    }

    public Long getAppointmentSlotId() {
        return appointmentSlotId;
    }

    public void setAppointmentSlotId(Long appointmentSlotId) {
        this.appointmentSlotId = appointmentSlotId;
    }

    public Long getBarberId() {
        return barberId;
    }

    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



    public AppointmentSlot(Long appointmentSlotId, Long barberId, String startTime, String endTime) {
        this.appointmentSlotId = appointmentSlotId;
        this.barberId = barberId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "{" +
                "\"appointmentSlotId\"=" + "\"" + appointmentSlotId + "\"" +
                ", \"barberId\"=" + "\"" + barberId + "\"" +
                ", \"startTime\"='" + "\"" + startTime + "\"" +
                ", \"endTime\"='" + "\"" + endTime + "\"" +
                ", \"date\"=" + "\"" + date + "\"" +
                '}';
    }
}
