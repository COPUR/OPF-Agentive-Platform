package com.xbank.opf.sdk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.xbank.opf.sdk.mapper.NebrasIntentMapper;

import com.xbank.opf.sdk.domain.SalaryBatchRequest;
import com.xbank.opf.sdk.domain.CarLeaseLoanRequest;

public class NebrasIntentMapperTest {

    @Test
    public void testSalaryBatchMapping() {
        SalaryBatchRequest request = new SalaryBatchRequest("9934123", 50, 10000.00);
        String jsonIntent = NebrasIntentMapper.mapToOpenFinanceIntent(request);
        
        assertTrue(jsonIntent.contains("\"intent\": \"DOMESTIC_PAYMENT\""));
        assertTrue(jsonIntent.contains("\"amount\": 10000.00"));
        assertTrue(jsonIntent.contains("\"type\": \"SALARY_BATCH\""));
    }

    @Test
    public void testCarLeaseMapping() {
        CarLeaseLoanRequest request = new CarLeaseLoanRequest("L-8392", "9934123", 2500.00);
        String jsonIntent = NebrasIntentMapper.mapToOpenFinanceIntent(request);
        
        assertTrue(jsonIntent.contains("\"intent\": \"DOMESTIC_PAYMENT\""));
        assertTrue(jsonIntent.contains("\"type\": \"CAR_LEASE\""));
    }
}
