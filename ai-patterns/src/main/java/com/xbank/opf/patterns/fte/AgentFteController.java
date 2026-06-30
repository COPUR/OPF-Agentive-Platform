package com.xbank.opf.patterns.fte;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fte")
public class AgentFteController {

    private final FteHarnessCoordinator coordinator;

    public AgentFteController(FteHarnessCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @PostMapping("/{role}/execute")
    public ResponseEntity<Map<String, String>> executeFteTask(
            @PathVariable("role") String roleStr,
            @RequestBody Map<String, String> payload) {

        try {
            AgentFteRole role = AgentFteRole.valueOf(roleStr.toUpperCase());
            String prompt = payload.getOrDefault("prompt", "");
            
            if (prompt.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Prompt is required"));
            }

            String response = coordinator.dispatchTask(role, prompt);
            return ResponseEntity.ok(Map.of(
                    "role", role.name(),
                    "response", response
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid AgentFteRole: " + roleStr));
        }
    }
}
