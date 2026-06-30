package com.xbank.opf.patterns.fte.scrum.workflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ScrumCeremoniesWorkflow {

    @WorkflowMethod
    String executeScrumCycle(String sprintContext);

    @SignalMethod
    void approveScrumReport(boolean approved);
}
