package com.xbank.opf.cognitive.adapter.in.web;

import io.temporal.client.WorkflowClient;
import com.xbank.opf.cognitive.adapter.out.temporal.OpenFinanceWorkflow;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class OpenFinanceControllerTest {

    @Test
    public void testGetAccounts() {
        WorkflowClient mockClient = Mockito.mock(WorkflowClient.class);
        OpenFinanceWorkflow mockWorkflow = Mockito.mock(OpenFinanceWorkflow.class);
        
        Mockito.when(mockClient.newWorkflowStub(eq(OpenFinanceWorkflow.class), any(io.temporal.client.WorkflowOptions.class)))
               .thenReturn(mockWorkflow);
        Mockito.when(mockWorkflow.fetchAccountsIntent(any()))
               .thenReturn("MOCKED_ACCOUNTS");

        OpenFinanceController controller = new OpenFinanceController(mockClient);
        Map<String, Object> response = controller.getAccounts("Bearer token");
        
        assertNotNull(response);
        Map<String, Object> data = (Map<String, Object>) response.get("Data");
        assertEquals("MOCKED_ACCOUNTS", data.get("Account"));
    }
}
