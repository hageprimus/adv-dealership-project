package com.dealership.contracts;

import com.dealership.vehicles.Vehicle;

import java.math.BigDecimal;

public class LeaseContract extends Contract {
    private static final double END_VALUE_FACTOR = 0.5;
    private static final double LEASE_FEE_FACTOR = 0.07;

    private double apr;
    private int loanLength;
    public LeaseContract(String date, String name, String email, Vehicle vehicle, double apr, int loanLength) {
        super(date, name, email, vehicle);
        this.apr = apr;
        this.loanLength = loanLength;
    }

    public BigDecimal getExpectedEndValue() {
        return vehicle.getPrice().multiply(BigDecimal.valueOf(END_VALUE_FACTOR));
    }

    public BigDecimal getLeaseFee() {
        return vehicle.getPrice().multiply(BigDecimal.valueOf(LEASE_FEE_FACTOR));
    }

    public BigDecimal getTotalInterest() {
        return getTotalPrice().multiply(BigDecimal.valueOf(apr)).multiply(BigDecimal.valueOf(loanLength));
    }

    @Override
    public BigDecimal getTotalPrice() {
        return getExpectedEndValue().add(getLeaseFee());
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        BigDecimal totalLease = getTotalPrice().add(getTotalInterest()).add(getLeaseFee());
        return totalLease.divide(BigDecimal.valueOf(loanLength));
    }
}
