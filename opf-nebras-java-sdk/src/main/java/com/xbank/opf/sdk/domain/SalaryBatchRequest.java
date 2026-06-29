package com.xbank.opf.sdk.domain;

public class SalaryBatchRequest {
    private String corporateAccountId;
    private int totalEmployees;
    private double totalAmount;

    public SalaryBatchRequest(String corporateAccountId, int totalEmployees, double totalAmount) {
        this.corporateAccountId = corporateAccountId;
        this.totalEmployees = totalEmployees;
        this.totalAmount = totalAmount;
    }

    public String getCorporateAccountId() { return corporateAccountId; }
    public int getTotalEmployees() { return totalEmployees; }
    public double getTotalAmount() { return totalAmount; }
}
