package com.xbank.opf.patterns.harness;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.economics.FteEconomicsProfile;
import com.xbank.opf.patterns.fte.identity.domain.AgentIdentityProfile;

import java.util.HashMap;
import java.util.Map;

public class AgentFteData {
    private AgentFteRole role;
    private String fteName;
    private AgentIdentityProfile identityProfile;
    private FteEconomicsProfile economicsProfile = new FteEconomicsProfile();
    private Map<String, Integer> employeeSkills = new HashMap<>();
    private double efficiencyScore = 1.0;

    public AgentFteData() {}

    public AgentFteData(AgentFteRole role, String fteName) {
        this.role = role;
        this.fteName = fteName;
    }

    public AgentFteRole getRole() {
        return role;
    }

    public void setRole(AgentFteRole role) {
        this.role = role;
    }

    public AgentIdentityProfile getIdentityProfile() {
        return identityProfile;
    }

    public void setIdentityProfile(AgentIdentityProfile identityProfile) {
        this.identityProfile = identityProfile;
    }

    public String getFteName() {
        return fteName;
    }

    public void setFteName(String fteName) {
        this.fteName = fteName;
    }

    public FteEconomicsProfile getEconomicsProfile() {
        return economicsProfile;
    }

    public void setEconomicsProfile(FteEconomicsProfile economicsProfile) {
        this.economicsProfile = economicsProfile;
    }

    public Map<String, Integer> getEmployeeSkills() {
        return employeeSkills;
    }

    public void setEmployeeSkills(Map<String, Integer> employeeSkills) {
        this.employeeSkills = employeeSkills;
    }

    public double getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(double efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }

    public void updateSkill(String skillName, int level) {
        this.employeeSkills.put(skillName, level);
    }
}
