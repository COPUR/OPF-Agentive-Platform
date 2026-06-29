package com.xbank.opf.cognitive.adapter.out.temporal;

import io.temporal.workflow.Workflow;
import java.time.Duration;

public class TppAdmissionWorkflowImpl implements TppAdmissionWorkflow {

    private boolean adminDecisionReceived = false;
    private boolean isApproved = false;
    private boolean signatureReceived = false;
    private String signatureToken = null;

    @Override
    public String processTppApplication(String tppName, String regulatoryId, String requestedScope) {
        
        // Agent 1 & 2: Comprehensive Risk Assessment & BGC (Mock logic)
        System.out.println("Running Deep Risk Assessment & AML/KYC BGC for TPP: " + tppName);
        boolean isHighRisk = regulatoryId == null || regulatoryId.startsWith("UNKNOWN");
        boolean failedBgc = tppName.toLowerCase().contains("sanctioned");
        
        if (isHighRisk || failedBgc) {
            System.out.println("BGC/Risk Failure detected. Awaiting Admin Human-in-the-Loop Override...");
            // Wait for Human-in-the-Loop Signal
            Workflow.await(Duration.ofDays(7), () -> adminDecisionReceived);
            
            if (!isApproved) {
                return "APPLICATION_REJECTED_BY_ADMIN";
            }
        }
        
        // Agent 3: Contract Generation
        System.out.println("Generating User Agreement Template for signature...");
        // In a full implementation, the UserAgreementService would be invoked via Temporal Activities
        
        // Wait for Cryptographic Signature from Frontend
        System.out.println("Awaiting Developer Cryptographic Signature...");
        Workflow.await(Duration.ofDays(14), () -> signatureReceived);
        
        if (signatureToken == null || signatureToken.isEmpty()) {
            return "APPLICATION_ABORTED_NO_SIGNATURE";
        }
        
        // Agent 4: Trigger Outbox Intent to Provision in Keycloak
        System.out.println("Valid Signature Received. Executing intent to provision TPP: " + tppName);
        return "TPP_PROVISIONED_SUCCESSFULLY";
    }

    @Override
    public void adminReviewDecision(boolean approved, String reason) {
        this.isApproved = approved;
        this.adminDecisionReceived = true;
    }

    @Override
    public void submitSignature(String token) {
        this.signatureToken = token;
        this.signatureReceived = true;
    }
}
