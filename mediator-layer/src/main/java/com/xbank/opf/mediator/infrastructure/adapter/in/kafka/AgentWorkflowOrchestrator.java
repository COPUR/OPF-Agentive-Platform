package com.xbank.opf.mediator.infrastructure.adapter.in.kafka;

import com.xbank.opf.mediator.application.service.AgentWorkflowService;
import org.springframework.stereotype.Component;

@Component
public class AgentWorkflowOrchestrator {

    private final AgentWorkflowService agentWorkflowService;

    public AgentWorkflowOrchestrator(AgentWorkflowService agentWorkflowService) {
        this.agentWorkflowService = agentWorkflowService;
    }

    public void onWorkflowEvent(String intentType) {
        agentWorkflowService.executeIntentWorkflow(intentType);
    }
}
