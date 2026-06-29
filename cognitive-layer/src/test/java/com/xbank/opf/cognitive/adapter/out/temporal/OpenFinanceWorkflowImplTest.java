package com.xbank.opf.cognitive.adapter.out.temporal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OpenFinanceWorkflowImplTest {

    @Test
    public void testFetchAccountsIntent() {
        OpenFinanceWorkflowImpl workflow = new OpenFinanceWorkflowImpl();
        String result = workflow.fetchAccountsIntent("user-123");
        
        assertNotNull(result);
        assertTrue(result.contains("user-123"));
        assertTrue(result.contains("[MOCKED]"));
    }
}
