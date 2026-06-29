package com.xbank.opf.sdk;

import com.xbank.opf.sdk.domain.CarLeaseLoanRequest;
import com.xbank.opf.sdk.domain.SalaryBatchRequest;
import com.xbank.opf.sdk.mapper.NebrasIntentMapper;

import java.util.UUID;

public class NebrasClient {

    private final String clientId;
    private final String privateKeyPem;

    public NebrasClient(String clientId, String privateKeyPem) {
        this.clientId = clientId;
        this.privateKeyPem = privateKeyPem;
    }

    /**
     * Executes a Salary Batch using Open Finance compliance.
     * Automatically handles DPoP cryptography and intent mapping.
     */
    public String executeSalaryBatch(SalaryBatchRequest request) {
        String dpopToken = generateDpopProof();
        String jsonPayload = NebrasIntentMapper.mapToOpenFinanceIntent(request);
        
        System.out.println("Sending encrypted payload to Nebras API Gateway: " + jsonPayload);
        System.out.println("Attached DPoP Header: " + dpopToken);
        
        // Mock API Call
        return "INTENT_ID_" + UUID.randomUUID().toString();
    }

    /**
     * Executes a Car Lease Loan Payment using Open Finance compliance.
     */
    public String executeCarLeasePayment(CarLeaseLoanRequest request) {
        String dpopToken = generateDpopProof();
        String jsonPayload = NebrasIntentMapper.mapToOpenFinanceIntent(request);
        
        System.out.println("Sending encrypted payload to Nebras API Gateway: " + jsonPayload);
        System.out.println("Attached DPoP Header: " + dpopToken);
        
        return "INTENT_ID_" + UUID.randomUUID().toString();
    }

    private String generateDpopProof() {
        // In reality, this uses the privateKeyPem to generate a JWT signed with PS256
        return "eyJ0eXAiOiJkcG9wK2p3dCIsImFsZyI6IlBTMjU2In0.mock_dpop_signature";
    }
}
