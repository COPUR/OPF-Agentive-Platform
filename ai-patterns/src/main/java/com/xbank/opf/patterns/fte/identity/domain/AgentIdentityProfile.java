package com.xbank.opf.patterns.fte.identity.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain model representing the organizational identity and decoupled credentials
 * of an Agent-FTE, mirroring a human FTE in the RBAC/IDP ecosystem.
 */
public class AgentIdentityProfile {

    private String corporateEmail;
    private String employeeId;
    private String githubUsername;
    private String jiraAccountId;
    private String credentialVaultPath;
    private Set<String> rbacRoles = new HashSet<>();

    public AgentIdentityProfile() {}

    public AgentIdentityProfile(String corporateEmail, String employeeId, String githubUsername, String jiraAccountId, String credentialVaultPath, Set<String> rbacRoles) {
        this.corporateEmail = corporateEmail;
        this.employeeId = employeeId;
        this.githubUsername = githubUsername;
        this.jiraAccountId = jiraAccountId;
        this.credentialVaultPath = credentialVaultPath;
        this.rbacRoles = rbacRoles != null ? rbacRoles : new HashSet<>();
    }

    public AgentIdentityProfile(String corporateEmail, String employeeId, String githubUsername, String jiraAccountId, String credentialVaultPath) {
        this(corporateEmail, employeeId, githubUsername, jiraAccountId, credentialVaultPath, new HashSet<>());
    }

    public void assignRole(String role) {
        this.rbacRoles.add(role);
    }

    public String getCorporateEmail() { return corporateEmail; }
    public void setCorporateEmail(String corporateEmail) { this.corporateEmail = corporateEmail; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getGithubUsername() { return githubUsername; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }

    public String getJiraAccountId() { return jiraAccountId; }
    public void setJiraAccountId(String jiraAccountId) { this.jiraAccountId = jiraAccountId; }

    public String getCredentialVaultPath() { return credentialVaultPath; }
    public void setCredentialVaultPath(String credentialVaultPath) { this.credentialVaultPath = credentialVaultPath; }

    public Set<String> getRbacRoles() { return rbacRoles; }
    public void setRbacRoles(Set<String> rbacRoles) { this.rbacRoles = rbacRoles; }

    public boolean hasRole(String role) {
        return this.rbacRoles.contains(role);
    }
}
