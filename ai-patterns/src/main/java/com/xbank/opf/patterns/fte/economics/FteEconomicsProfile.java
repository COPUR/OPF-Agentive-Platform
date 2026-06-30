package com.xbank.opf.patterns.fte.economics;

import java.math.BigDecimal;

public class FteEconomicsProfile {
    
    private BigDecimal currentBudget;
    private BigDecimal lifetimeConsumption;

    public FteEconomicsProfile() {
        this.currentBudget = BigDecimal.ZERO;
        this.lifetimeConsumption = BigDecimal.ZERO;
    }

    public FteEconomicsProfile(BigDecimal initialBudget) {
        this.currentBudget = initialBudget;
        this.lifetimeConsumption = BigDecimal.ZERO;
    }

    public BigDecimal getCurrentBudget() {
        return currentBudget;
    }

    public void setCurrentBudget(BigDecimal currentBudget) {
        this.currentBudget = currentBudget;
    }

    public BigDecimal getLifetimeConsumption() {
        return lifetimeConsumption;
    }

    public void setLifetimeConsumption(BigDecimal lifetimeConsumption) {
        this.lifetimeConsumption = lifetimeConsumption;
    }

    public void consume(BigDecimal cost) {
        this.currentBudget = this.currentBudget.subtract(cost);
        this.lifetimeConsumption = this.lifetimeConsumption.add(cost);
    }
}
