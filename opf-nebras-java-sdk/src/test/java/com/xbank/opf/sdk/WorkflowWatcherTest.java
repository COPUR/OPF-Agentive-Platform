package com.xbank.opf.sdk;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WorkflowWatcherTest {

    @Test
    public void testPolling() {
        WorkflowWatcher watcher = new WorkflowWatcher();
        String result = watcher.waitForCompletion("intent-12345");
        assertNotNull(result);
        assertEquals("COMPLETED_SUCCESSFULLY", result);
    }
}
