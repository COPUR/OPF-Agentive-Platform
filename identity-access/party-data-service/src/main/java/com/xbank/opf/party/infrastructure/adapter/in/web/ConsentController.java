package com.xbank.opf.party.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/sca")
public class ConsentController {

    @PostMapping("/consent")
    public Map<String, String> generateConsent() {
        return Map.of(
            "consentId", "c-" + UUID.randomUUID().toString(),
            "status", "AwaitingAuthorisation"
        );
    }

    @DeleteMapping("/consent/{consentId}")
    public Map<String, String> revokeConsent(@PathVariable String consentId) {
        return Map.of(
            "consentId", consentId,
            "status", "Revoked"
        );
    }
}
