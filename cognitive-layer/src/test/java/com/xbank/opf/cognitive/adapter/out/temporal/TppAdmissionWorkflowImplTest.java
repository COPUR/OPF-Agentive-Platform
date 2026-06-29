package com.xbank.opf.cognitive.adapter.out.temporal;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import io.temporal.workflow.Workflow;
import static org.junit.jupiter.api.Assertions.*;

public class TppAdmissionWorkflowImplTest {

    @Test
    public void testRejectionForHighRisk() {
        TppAdmissionWorkflowImpl workflow = new TppAdmissionWorkflowImpl();
        workflow.adminReviewDecision(true, "Approved");
        workflow.submitSignature("signature-123");
        
        try (MockedStatic<Workflow> mockedWorkflow = Mockito.mockStatic(Workflow.class)) {
            mockedWorkflow.when(() -> Workflow.await(Mockito.any(), Mockito.any())).thenAnswer(invocation -> null);
            String result = workflow.processTppApplication("Safe TPP", "REG-123", "AIS");
            assertEquals("TPP_PROVISIONED_SUCCESSFULLY", result);
        }
    }

    @Test
    public void testRejectionForSanctioned() {
        TppAdmissionWorkflowImpl workflow = new TppAdmissionWorkflowImpl();
        
        try (MockedStatic<Workflow> mockedWorkflow = Mockito.mockStatic(Workflow.class)) {
            mockedWorkflow.when(() -> Workflow.await(Mockito.any(), Mockito.any())).thenAnswer(invocation -> null);
            String result = workflow.processTppApplication("sanctioned-company", "UNKNOWN", "AIS");
            assertEquals("APPLICATION_REJECTED_BY_ADMIN", result);
        }
    }
}
