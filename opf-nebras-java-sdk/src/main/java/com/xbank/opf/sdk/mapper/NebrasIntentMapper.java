package com.xbank.opf.sdk.mapper;

import com.xbank.opf.sdk.domain.CarLeaseLoanRequest;
import com.xbank.opf.sdk.domain.SalaryBatchRequest;

public class NebrasIntentMapper {

    /**
     * Translates a legacy Salary Batch Request into an Open Finance Domestic Payment Intent Payload.
     */
    public static String mapToOpenFinanceIntent(SalaryBatchRequest request) {
        return String.format(
            "{\"intent\": \"DOMESTIC_PAYMENT\", \"type\": \"SALARY_BATCH\", \"sourceAccount\": \"%s\", \"amount\": %f}", 
            request.getCorporateAccountId(), request.getTotalAmount()
        );
    }

    /**
     * Translates a legacy Car Lease Request into an Open Finance Domestic Payment Intent Payload.
     */
    public static String mapToOpenFinanceIntent(CarLeaseLoanRequest request) {
        return String.format(
            "{\"intent\": \"DOMESTIC_PAYMENT\", \"type\": \"CAR_LEASE\", \"sourceAccount\": \"%s\", \"contractRef\": \"%s\", \"amount\": %f}", 
            request.getCustomerAccountId(), request.getLeaseContractId(), request.getMonthlyInstallment()
        );
    }
}
