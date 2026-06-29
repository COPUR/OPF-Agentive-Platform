package com.xbank.opf.cognitive.adapter.in.web;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import com.xbank.opf.cognitive.adapter.out.temporal.OpenFinanceWorkflow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/open-banking/v3.1")
public class OpenFinanceController {

    private WorkflowClient workflowClient;

    public OpenFinanceController() {
        try {
            this.workflowClient = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs());
        } catch (Exception e) {
            // Ignored if local Temporal isn't running during startup
        }
    }

    public OpenFinanceController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @GetMapping("/aisp/accounts")
    public Map<String, Object> getAccounts(@RequestHeader(value="Authorization", required=false) String authHeader) {
        
        // 1. Initialize Temporal Workflow
        OpenFinanceWorkflow workflow = workflowClient.newWorkflowStub(
                OpenFinanceWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("OPEN_FINANCE_QUEUE")
                        .setWorkflowId("accounts-fetch-" + UUID.randomUUID().toString())
                        .build()
        );

        // 2. Trigger Workflow synchronously (or async depending on architecture)
        // Here we block for simplicity of the REST response
        String workflowResponse = workflow.fetchAccountsIntent("userId-1234");

        return Map.of(
                "Data", Map.of("Account", workflowResponse),
                "Links", Map.of("Self", "/open-banking/v3.1/aisp/accounts"),
                "Meta", Map.of("TotalPages", 1)
        );
    }
}
