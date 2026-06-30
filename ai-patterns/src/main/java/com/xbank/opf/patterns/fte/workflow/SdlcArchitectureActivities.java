package com.xbank.opf.patterns.fte.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SdlcArchitectureActivities {

    @ActivityMethod
    String executeIngestion(String jiraPrdInput);

    @ActivityMethod
    String executeTopology(String ingestedContext);

    @ActivityMethod
    String executeCompliance(String lldContext);
}
