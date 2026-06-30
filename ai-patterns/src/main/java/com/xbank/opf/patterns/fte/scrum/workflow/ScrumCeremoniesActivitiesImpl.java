package com.xbank.opf.patterns.fte.scrum.workflow;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteHarnessCoordinator;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Component
public class ScrumCeremoniesActivitiesImpl implements ScrumCeremoniesActivities {

    private final FteHarnessCoordinator coordinator;

    public ScrumCeremoniesActivitiesImpl(FteHarnessCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public String runSprintPlanning(String backlog) {
        System.out.println("Executing Sprint Planning...");
        return dispatchToAllAgents("Sprint Planning: Analyze the following backlog and state your commitments considering your capabilities. Backlog: " + backlog);
    }

    @Override
    public String runDailyStandup(String sprintGoal) {
        System.out.println("Executing Daily Standup...");
        return dispatchToAllAgents("Daily Standup: What did you do yesterday, what will you do today, and are there any blockers? Sprint Goal: " + sprintGoal);
    }

    @Override
    public String runSprintReview(String deliverable) {
        System.out.println("Executing Sprint Review...");
        return dispatchToAllAgents("Sprint Review: Evaluate the physical running code against confluence specs for the following deliverable: " + deliverable);
    }

    @Override
    public String runSprintRetrospective(String performanceMetrics) {
        System.out.println("Executing Sprint Retrospective...");
        return dispatchToAllAgents("Sprint Retrospective: Compute DORA metrics and identify engineering bottlenecks based on: " + performanceMetrics);
    }

    @Override
    public String generateScrumReport(String planning, String standup, String review, String retro) {
        System.out.println("Compiling Scrum Report...");
        return String.format(
                "--- OPF SCRUM REPORT ---\n\n" +
                "1. SPRINT PLANNING:\n%s\n\n" +
                "2. DAILY STANDUP:\n%s\n\n" +
                "3. SPRINT REVIEW:\n%s\n\n" +
                "4. SPRINT RETROSPECTIVE:\n%s\n\n" +
                "------------------------",
                planning, standup, review, retro
        );
    }

    private String dispatchToAllAgents(String prompt) {
        StringJoiner sj = new StringJoiner("\n---\n");
        for (AgentFteRole role : AgentFteRole.values()) {
            try {
                String response = coordinator.dispatchTask(role, prompt);
                sj.add(role.name() + " Response:\n" + response);
            } catch (Exception e) {
                sj.add(role.name() + " Error:\n" + e.getMessage());
            }
        }
        return sj.toString();
    }
}
