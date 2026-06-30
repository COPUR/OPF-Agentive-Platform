package com.xbank.opf.patterns.fte.web;

import com.xbank.opf.patterns.fte.scrum.workflow.ScrumCeremoniesWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fte/scrum")
public class FteScrumController {

    private final WorkflowClient workflowClient;

    public FteScrumController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startScrumCycle(@RequestBody Map<String, String> request) {
        String sprintContext = request.getOrDefault("sprintContext", "Default Sprint Context");
        String workflowId = "scrum-workflow-" + UUID.randomUUID().toString();

        ScrumCeremoniesWorkflow workflow = workflowClient.newWorkflowStub(
                ScrumCeremoniesWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setTaskQueue("FTE_SCRUM_TASK_QUEUE")
                        .build()
        );

        // Start asynchronously
        WorkflowClient.start(workflow::executeScrumCycle, sprintContext);

        return ResponseEntity.ok(Map.of(
                "status", "STARTED",
                "workflowId", workflowId,
                "message", "Scrum Ceremonies Workflow has been successfully dispatched."
        ));
    }

    @PostMapping("/{workflowId}/approve")
    public ResponseEntity<Map<String, String>> approveScrumReport(@PathVariable String workflowId, @RequestParam boolean approved) {
        ScrumCeremoniesWorkflow workflow = workflowClient.newWorkflowStub(
                ScrumCeremoniesWorkflow.class,
                workflowId
        );

        // Send the signal
        workflow.approveScrumReport(approved);

        return ResponseEntity.ok(Map.of(
                "status", "SIGNAL_SENT",
                "workflowId", workflowId,
                "approved", String.valueOf(approved),
                "message", "HITL approval signal sent successfully."
        ));
    }
}
