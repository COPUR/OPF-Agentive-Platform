package com.xbank.opf.cognitive.adapter.in.web;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class DeveloperPortalControllerTest {

    @Test
    public void testSubmitApplication() {
        DeveloperPortalController controller = new DeveloperPortalController();
        Map<String, String> payload = Map.of("name", "Test TPP");
        
        Map<String, String> response = controller.submitApplication(payload);
        
        assertEquals("ADMISSION_WORKFLOW_STARTED", response.get("status"));
        assertTrue(response.get("workflowId").startsWith("tpp-onboard-"));
    }
}
