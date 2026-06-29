package com.xbank.opf.sdk.domain;

public class CarLeaseLoanRequest {
    private String leaseContractId;
    private String customerAccountId;
    private double monthlyInstallment;

    public CarLeaseLoanRequest(String leaseContractId, String customerAccountId, double monthlyInstallment) {
        this.leaseContractId = leaseContractId;
        this.customerAccountId = customerAccountId;
        this.monthlyInstallment = monthlyInstallment;
    }

    public String getLeaseContractId() { return leaseContractId; }
    public String getCustomerAccountId() { return customerAccountId; }
    public double getMonthlyInstallment() { return monthlyInstallment; }
}
