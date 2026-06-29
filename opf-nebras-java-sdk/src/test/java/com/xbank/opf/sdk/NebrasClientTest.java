package com.xbank.opf.sdk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.xbank.opf.sdk.domain.SalaryBatchRequest;
import com.xbank.opf.sdk.domain.CarLeaseLoanRequest;

public class NebrasClientTest {

    @Test
    public void testClientSalaryBatch() {
        NebrasClient client = new NebrasClient("clientId", "privateKeyPem");
        SalaryBatchRequest req = new SalaryBatchRequest("123", 10, 1000.0);
        String intentId = client.executeSalaryBatch(req);
        assertNotNull(intentId);
        assertTrue(intentId.startsWith("INTENT_ID_"));
    }

    @Test
    public void testClientCarLease() {
        NebrasClient client = new NebrasClient("clientId", "privateKeyPem");
        CarLeaseLoanRequest req = new CarLeaseLoanRequest("L-123", "C-123", 500.0);
        String intentId = client.executeCarLeasePayment(req);
        assertNotNull(intentId);
        assertTrue(intentId.startsWith("INTENT_ID_"));
    }
}
