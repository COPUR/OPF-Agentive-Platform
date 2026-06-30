package com.xbank.opf.patterns.fte;

import com.xbank.opf.patterns.fte.config.FteProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class AgentFteKafkaListener {

    private final FteHarnessCoordinator coordinator;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FteProperties properties;

    public AgentFteKafkaListener(FteHarnessCoordinator coordinator, KafkaTemplate<String, Object> kafkaTemplate, FteProperties properties) {
        this.coordinator = coordinator;
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @KafkaListener(topics = "${opf.fte.kafka.execute-topic:fte.task.execute}", groupId = "${opf.fte.kafka.group-id:agent-fte-group}")
    public void consumeTask(Map<String, String> payload) {
        String roleStr = payload.get("role");
        String prompt = payload.get("prompt");
        String correlationId = payload.getOrDefault("correlationId", UUID.randomUUID().toString());
        String resultTopic = properties.getKafka() != null && properties.getKafka().getResultTopic() != null 
                ? properties.getKafka().getResultTopic() 
                : "fte.task.result";

        try {
            if (roleStr != null && prompt != null) {
                AgentFteRole role = AgentFteRole.valueOf(roleStr.toUpperCase());
                String response = coordinator.dispatchTask(role, prompt);

                // Publish result
                kafkaTemplate.send(resultTopic, Map.of(
                        "correlationId", correlationId,
                        "role", role.name(),
                        "status", "SUCCESS",
                        "response", response
                ));
            }
        } catch (Exception e) {
            // Publish failure
            kafkaTemplate.send(resultTopic, Map.of(
                    "correlationId", correlationId,
                    "status", "FAILURE",
                    "error", e.getMessage()
            ));
        }
    }
}
