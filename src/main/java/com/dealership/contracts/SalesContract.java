package com.dealership.contracts;

import com.dealership.vehicles.Vehicle;

import java.math.BigDecimal;

public class SalesContract extends Contract {
    private boolean financed;
    private double salesTax;
    private double apr;
    private int recordingFee;
    private int processingFee;
    private int loanLength;

    public SalesContract(String date, String name, String email, Vehicle vehicle, boolean financed, double salesTax, double apr, int recordingFee, int processingFee, int loanLength) {
        super(date, name, email, vehicle);
        this.financed = financed;
        this.salesTax = salesTax;
        this.apr = apr;
        this.recordingFee = recordingFee;
        this.processingFee = processingFee;
        this.loanLength = loanLength;
    }

    //region Getters/Setters
    public boolean isFinanced() {
        return financed;
    }

    public void setFinanced(boolean financed) {
        this.financed = financed;
    }

    public double getSalesTax() {
        return salesTax;
    }

    public void setSalesTax(double salesTax) {
        this.salesTax = salesTax;
    }

    public double getApr() {
        return apr;
    }

    public void setApr(double apr) {
        this.apr = apr;
    }

    public int getRecordingFee() {
        return recordingFee;
    }

    public void setRecordingFee(int recordingFee) {
        this.recordingFee = recordingFee;
    }

    public int getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(int processingFee) {
        this.processingFee = processingFee;
    }

    public int getLoanLength() {
        return loanLength;
    }

    public void setLoanLength(int loanLength) {
        this.loanLength = loanLength;
    }
    //endregion

    @Override
    public BigDecimal getTotalPrice() {
        BigDecimal vehicleTax = vehicle.getPrice().multiply(BigDecimal.valueOf(salesTax));
        BigDecimal processing = BigDecimal.valueOf(processingFee);
        return vehicle.getPrice().add(vehicleTax).add(BigDecimal.valueOf(recordingFee)).add(processing);
    }

    @Override
    public BigDecimal getMonthlyPayment() {
        if (!financed) return BigDecimal.ZERO;
        BigDecimal aprConvert = BigDecimal.valueOf(apr);
        BigDecimal factor = aprConvert.multiply(aprConvert.add(BigDecimal.ONE));
        return getTotalPrice().multiply(aprConvert.multiply(factor.pow(loanLength)).divide(factor.pow(loanLength).subtract(BigDecimal.ONE)));
    }
}
