package com.xbank.opf.patterns.fte.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SdlcArchitectureWorkflow {

    @WorkflowMethod
    String executeEndToEndArchitecture(String jiraPrdInput);
}
