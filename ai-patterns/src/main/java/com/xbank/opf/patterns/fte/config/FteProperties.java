package com.xbank.opf.patterns.fte.config;

import com.xbank.opf.patterns.fte.AgentFteRole;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "opf.fte")
public class FteProperties {

    private final Economics economics = new Economics();
    private final Kafka kafka = new Kafka();
    private Map<AgentFteRole, String> prompts;

    public Economics getEconomics() {
        return economics;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public Map<AgentFteRole, String> getPrompts() {
        return prompts;
    }

    public void setPrompts(Map<AgentFteRole, String> prompts) {
        this.prompts = prompts;
    }

    public static class Economics {
        private BigDecimal initialBudget;
        private BigDecimal costLow;
        private BigDecimal costMedium;
        private BigDecimal costHigh;

        public BigDecimal getInitialBudget() {
            return initialBudget;
        }

        public void setInitialBudget(BigDecimal initialBudget) {
            this.initialBudget = initialBudget;
        }

        public BigDecimal getCostLow() {
            return costLow;
        }

        public void setCostLow(BigDecimal costLow) {
            this.costLow = costLow;
        }

        public BigDecimal getCostMedium() {
            return costMedium;
        }

        public void setCostMedium(BigDecimal costMedium) {
            this.costMedium = costMedium;
        }

        public BigDecimal getCostHigh() {
            return costHigh;
        }

        public void setCostHigh(BigDecimal costHigh) {
            this.costHigh = costHigh;
        }
    }

    public static class Kafka {
        private String executeTopic;
        private String resultTopic;
        private String groupId;

        public String getExecuteTopic() {
            return executeTopic;
        }

        public void setExecuteTopic(String executeTopic) {
            this.executeTopic = executeTopic;
        }

        public String getResultTopic() {
            return resultTopic;
        }

        public void setResultTopic(String resultTopic) {
            this.resultTopic = resultTopic;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }
}
