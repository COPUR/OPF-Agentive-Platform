package com.xbank.opf.cognitive.adapter.out.temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OpenFinanceWorkflow {

    @WorkflowMethod
    String fetchAccountsIntent(String userId);

}
