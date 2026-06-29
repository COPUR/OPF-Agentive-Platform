package com.xbank.opf.cognitive.adapter.out.temporal;

import com.xbank.opf.cognitive.domain.RequirementDocument;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface OpenFinanceWorkflow {

    @WorkflowMethod
    List<String> executeAgenticWorkflow(RequirementDocument requirementDocument);
}
