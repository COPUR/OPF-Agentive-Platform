package com.xbank.opf.patterns.fte.economics;

import com.xbank.opf.patterns.fte.AgentFteRole;

import java.math.BigDecimal;

public class InsufficientFteBudgetException extends RuntimeException {
    
    private final AgentFteRole role;
    private final BigDecimal currentBudget;
    private final BigDecimal requiredCost;

    public InsufficientFteBudgetException(AgentFteRole role, BigDecimal currentBudget, BigDecimal requiredCost) {
        super(String.format("FTE %s has insufficient budget. Required: %s, Current: %s", role.name(), requiredCost, currentBudget));
        this.role = role;
        this.currentBudget = currentBudget;
        this.requiredCost = requiredCost;
    }

    public AgentFteRole getRole() {
        return role;
    }

    public BigDecimal getCurrentBudget() {
        return currentBudget;
    }

    public BigDecimal getRequiredCost() {
        return requiredCost;
    }
}
