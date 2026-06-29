package com.xbank.opf.cognitive.adapter.out.temporal;

import com.xbank.opf.cognitive.domain.RequirementDocument;
import com.xbank.opf.cognitive.domain.StructuredTask;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenFinanceWorkflowImpl implements OpenFinanceWorkflow {

    private final AgentExecutionActivity agentActivity = Workflow.newActivityStub(
            AgentExecutionActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
                    .build()
    );

    @Override
    public List<String> executeAgenticWorkflow(RequirementDocument requirementDocument) {
        // 1. Structured Requirements: Parse PRD to JSON Task List
        List<StructuredTask> tasks = agentActivity.parseRequirements(requirementDocument);
        
        List<String> results = new ArrayList<>();
        
        // 2. Iterative Looping over tasks
        for (StructuredTask task : tasks) {
            // 3. Isolated State Management: Each task executes independently without full history
            String result = agentActivity.executeIsolatedTask(task);
            results.add(result);
        }
        
        return results;
    }
}
