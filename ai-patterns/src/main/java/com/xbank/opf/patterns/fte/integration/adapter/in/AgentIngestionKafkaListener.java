package com.xbank.opf.patterns.fte.integration.adapter.in;

import com.xbank.opf.patterns.fte.AgentFteRole;
import com.xbank.opf.patterns.fte.FteHarnessCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AgentIngestionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(AgentIngestionKafkaListener.class);
    
    private final FteHarnessCoordinator fteHarnessCoordinator;

    public AgentIngestionKafkaListener(FteHarnessCoordinator fteHarnessCoordinator) {
        this.fteHarnessCoordinator = fteHarnessCoordinator;
    }

    /**
     * Consumes events from the OpenFinance topic and dispatches them 
     * directly to the AUTONOMOUS_INGESTION Agent-FTE for processing.
     */
    @KafkaListener(topics = "${opf.events.topic.ingestion:cbuae.openfinance.events}", groupId = "agent-fte-ingestion-group")
    public void consumeOpenFinanceEvent(String eventPayload) {
        log.info("Received OpenFinance event from Kafka. Dispatching to AUTONOMOUS_INGESTION agent...");
        
        String prompt = "Review the following incoming webhook payload for CBUAE open finance compliance and ingest the data into the semantic cache:\n" + eventPayload;
        
        try {
            String aiResponse = fteHarnessCoordinator.dispatchTask(AgentFteRole.AUTONOMOUS_INGESTION, prompt);
            log.info("AUTONOMOUS_INGESTION agent processed event successfully. AI Response: {}", aiResponse);
        } catch (Exception e) {
            log.error("Failed to process event using AUTONOMOUS_INGESTION agent: {}", e.getMessage(), e);
        }
    }
}
