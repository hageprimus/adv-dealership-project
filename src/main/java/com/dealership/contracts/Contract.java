package com.dealership.contracts;

import com.dealership.vehicles.Vehicle;

import java.math.BigDecimal;

public abstract class Contract {
    protected String date;
    protected String name;
    protected String email;
    protected Vehicle vehicle;

    public Contract(String date, String name, String email, Vehicle vehicle) {
        this.date = date;
        this.name = name;
        this.email = email;
        this.vehicle = vehicle;
    }

    //region Getters/Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    //endregion

    public abstract BigDecimal getTotalPrice();
    public abstract BigDecimal getMonthlyPayment();
}
