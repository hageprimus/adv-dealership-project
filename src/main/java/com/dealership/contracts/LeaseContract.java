package com.dealership.contracts;

import com.dealership.vehicles.Vehicle;

import java.math.BigDecimal;

public class LeaseContract extends Contract {
    public LeaseContract(String date, String name, String email, Vehicle vehicle) {
        super(date, name, email, vehicle);
    }

    @Override
    public BigDecimal getTotalPrice() {
        return null;
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        return null;
    }
}
