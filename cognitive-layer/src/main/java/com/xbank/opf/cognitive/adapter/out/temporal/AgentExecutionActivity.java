package com.xbank.opf.cognitive.adapter.out.temporal;

import com.xbank.opf.cognitive.domain.RequirementDocument;
import com.xbank.opf.cognitive.domain.StructuredTask;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

@ActivityInterface
public interface AgentExecutionActivity {

    @ActivityMethod
    List<StructuredTask> parseRequirements(RequirementDocument document);

    @ActivityMethod
    String executeIsolatedTask(StructuredTask task);
}
