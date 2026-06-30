package com.xbank.opf.patterns.fte.scrum.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ScrumCeremoniesActivities {

    @ActivityMethod
    String runSprintPlanning(String backlog);

    @ActivityMethod
    String runDailyStandup(String sprintGoal);

    @ActivityMethod
    String runSprintReview(String deliverable);

    @ActivityMethod
    String runSprintRetrospective(String performanceMetrics);

    @ActivityMethod
    String generateScrumReport(String planning, String standup, String review, String retro);
}
