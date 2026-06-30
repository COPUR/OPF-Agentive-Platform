package com.xbank.opf.party.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/keycloak")
public class TppProvisioningController {

    /**
     * Mock endpoint triggered by the Mediator Layer after the Cognitive Layer
     * successfully authorizes a TPP admission.
     */
    @PostMapping("/provision")
    public Map<String, String> provisionOAuthClient(@RequestBody Map<String, String> payload) {
        String tppName = payload.getOrDefault("tppName", "Default_TPP");
        
        // Generate mock Keycloak credentials
        String clientId = "client_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String clientSecret = UUID.randomUUID().toString();
        
        return Map.of(
            "tppName", tppName,
            "clientId", clientId,
            "clientSecret", clientSecret,
            "status", "ACTIVE"
        );
    }
}
