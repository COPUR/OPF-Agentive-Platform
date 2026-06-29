package com.xbank.opf.cognitive.adapter.out.temporal;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TppAdmissionWorkflow {

    /**
     * Starts the long-running process of evaluating a TPP's application.
     */
    @WorkflowMethod
    String processTppApplication(String tppName, String regulatoryId, String requestedScope);

    /**
     * Signal method for a Human-in-the-Loop admin to manually approve
     * or reject an application if the Agent flags it as high-risk.
     */
    @SignalMethod
    void adminReviewDecision(boolean isApproved, String reason);

    /**
     * Signal method for the TPP developer to asynchronously submit
     * their cryptographic signature for the User Agreement.
     */
    @SignalMethod
    void submitSignature(String signatureToken);
}
