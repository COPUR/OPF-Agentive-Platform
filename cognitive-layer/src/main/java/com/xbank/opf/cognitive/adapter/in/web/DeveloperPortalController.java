package com.xbank.opf.cognitive.adapter.in.web;

import com.xbank.opf.cognitive.adapter.out.temporal.TppAdmissionWorkflow;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/souq/tpps")
public class DeveloperPortalController {

    @PostMapping("/apply")
    public Map<String, String> submitApplication(@RequestBody Map<String, String> payload) {
        // In a real scenario, this would use Temporal WorkflowClient 
        // to start TppAdmissionWorkflow.class
        
        String tppName = payload.getOrDefault("name", "Unknown TPP");
        String workflowId = "tpp-onboard-" + UUID.randomUUID().toString();
        
        return Map.of(
            "status", "ADMISSION_WORKFLOW_STARTED",
            "workflowId", workflowId,
            "message", "The Cognitive Agents are currently analyzing your regulatory documents."
        );
    }
}
