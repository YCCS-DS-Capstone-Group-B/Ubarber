package com.ubarber.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@Entity
public class Client {
    @jakarta.persistence.Id
    @Id
    private Long id;
    @Column
    private String location;
    @Column
    private String firstName;
    @Column
    private String middleName;
    @Column
    private String lastName;
    @Column
    private String email;
    @Column
    private double latitude;
    @Column
    private double longitude;

    @Column
    private String geoHash;

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public Client() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = (String) location;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=" + id +
                ", \"location\"=\"" + location + '\"' +
                ", \"firstName\"=\"" + firstName + '\"' +
                ", \"middleName\"=\"" + middleName + '\"' +
                ", \"lastName\"=\"" + lastName + '\"' +
                ", \"email\"=\"" + email + '\"' +
                ", \"latitude\"=" + latitude +
                ", \"longitude\"=" + longitude +
                ", \"geoHash\"=\"" + geoHash + '\"' +
                '}';
    }
}
