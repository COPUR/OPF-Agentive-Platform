# API Definitions & AI Anatomy
**Project:** OPF-Agentive-Platform

This document outlines the API specifications required for UAECB OpenFinance compliance, alongside the detailed Tripartite AI Anatomy powering the Agentive workflows.

---

## Part 1: Open Finance API Endpoints

The API Gateway (Kong) exposes the following RESTful paths to the Developer Portal, heavily secured by DPoP and Keycloak validations.

### 1.1 Account Information Service Provider (AISP)
| Method | Endpoint | Description | Requires Consent |
| :--- | :--- | :--- | :--- |
| `GET` | `/open-banking/v3.1/aisp/accounts` | Retrieves a list of authorized accounts for the given user token. | Yes (AccountAccessConsent) |
| `GET` | `/open-banking/v3.1/aisp/accounts/{AccountId}/balances` | Fetches the real-time balance for a specific account. | Yes (ReadBalances) |
| `GET` | `/open-banking/v3.1/aisp/accounts/{AccountId}/transactions` | Fetches the transaction history for an account. | Yes (ReadTransactions) |

### 1.2 Payment Initiation Service Provider (PISP)
| Method | Endpoint | Description | Requires Consent |
| :--- | :--- | :--- | :--- |
| `POST` | `/open-banking/v3.1/pisp/domestic-payment-consents` | Initializes an intent to execute a domestic payment. | Yes |
| `POST` | `/open-banking/v3.1/pisp/domestic-payments` | Executes the payment post-SCA authorization. | Yes |

---

## Part 2: Webhooks (Asynchronous Notifications)

To prevent aggressive polling from third-party applications, the mediator layer emits asynchronous events.

| Endpoint | Payload Trigger | Description |
| :--- | :--- | :--- |
| `POST /webhooks/consent-revoked` | `{ "consentId": "...", "status": "Revoked" }` | Fired when a user revokes their SCA Consent inside the IdP portal. |
| `POST /webhooks/payment-status` | `{ "paymentId": "...", "status": "Settled" }` | Fired by the Saga Orchestrator when a Finacle payment clears successfully. |

---

## Part 3: Tripartite AI Anatomy (The Sandwich Pattern)

The Agentive platform is built on a "Sandwich" architecture, separating deterministic code execution from non-deterministic language generation.

### 3.1 The Model Layer (The "Meat")
- **Foundation:** Self-hosted Llama-3 endpoints configured via vLLM inference servers. This ensures absolute Data Sovereignty (no PII leaves the VPC).
- **Semantic Cache:** Queries are intercepted by `pgvector`. If an identical conceptual query was recently processed (e.g., "What is the status of payment X?"), the vectorized DB returns the cached answer in <20ms, bypassing inference CapEx entirely.

### 3.2 The Cognitive Layer (The "Brain")
- **Agentive Orchestration**: Powered by the **Temporal SDK** utilizing Multi-Agent Workflows rather than static Sagas.
- **Workflow Steps**:
  1. **Intent Extraction**: Model Cascade directs the query to either an SLM or massive LLM based on decision complexity.
  2. **Circuit Breaking (Graceful Degradation)**: If the foundational LLMs fail or hallucinate non-existent API parameters, the system gracefully falls back to deterministic rule-based algorithms rather than throwing fatal API errors.

### PII Scrubbing
If the LLM generates a response or requests a tool execution, it must be scrubbed before passing backward down the sandwich layer. The `DataSanitizer` module uses standard NLP parsing to strip remaining names or emails before the Agent returns a response.

## Model Context Protocol (MCP) Integration
The highest-level interface to our API Anatomy is the **MCP Server**. By wrapping our Open Finance tools (`nebras_initiate_consent`, `nebras_execute_salary_batch`) within the Model Context Protocol, we allow external LLM Agents (e.g., Claude) to interface directly with our Cognitive Layer without requiring manual API integration by third-party developers.

### 3.3 The Harness Layer (The "Guardrails")
- **Security LLM (Streaming DLP)**: Before ANY user payload enters the foundational models, a specialized Security LLM interceptor acts as an Agent Gateway to inspect and block Prompt Injections.
- **Data Obfuscator (Agent 3 Scoper)**: A strict Java interface that parses all outgoing prompts to the Model Layer and hashes PII (Account Numbers, EIDs) using SHA-256 before inference.
- **Productivity Telemetry**: Runs asynchronously to log anonymized DORA metrics from agent execution, ensuring psychological safety while providing deep flow insights.
