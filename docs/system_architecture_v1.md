# OPF-Agentive-Platform System Architecture

This document outlines the core architectural components of the Agentive-OpenFinance-BAAS platform. The goal is to convert legacy Banking as a Service (BaaS) interfaces into UAECB OpenFinance-compliant endpoints via an Agentive Interface.

## Frontend
- **Developer Portal**: "X-Bank-Souq"

## Security & Access Management
- **API Firewall**: CrowdStrike
- **Gateway**: Kong API Gateway + Security + DPoP
- **Identity Provider (IdP)**: Okta
- **Identity Access Manager**: KeyCloak
- **Party Data Management**: For Strong Customer Authentication (SCA) steps
1.  **Frontend Interface Layer**: A React/Vite SPA (`frontend-x-bank-souq`) serving as the API Souq Developer Portal. It provides a visual UI for SDK downloads, MCP Configuration, and Agentive TPP Onboarding.
2.  **API Gateway Layer**: Spring Cloud Gateway enforces security policies, verifies DPoP (Demonstrating Proof-of-Possession), and validates JWT tokens. It also routes requests based on intent rather than strictly static paths.
3.  **MCP Server Layer**: A standalone Node.js server (`opf-nebras-mcp-server`) that wraps the Open Finance API. It acts as an autonomous translation layer, allowing AI Agents (via Model Context Protocol) to execute banking tools natively.
4.  **Cognitive Layer (Temporal)**: This replaces traditional microservice orchestration. Temporal Workflows manage state, retries, and timeouts for LLM inferences. It interacts with the **Security LLM** (for DLP/PII filtering) and **Reasoning LLMs** (for logic).
5.  **Mediator Layer (CQRS/Event Sourcing)**: Uses an Outbox pattern with Apache Kafka to guarantee transaction delivery. It separates the "intent to pay" (Cognitive) from the "execution of payment" (Legacy).
- **Exposed Services**: MCP, standard REST API, WSDL for Legacy Banking API integrations.
- **Storage Strategy**: Golden CDC source replicated to a Silver Copy to support CQRS for Read Services.

## AI & Cognitive Layers
- **AI Patterns**: Sandwich + Harness + Model
- **Cognitive Layer**: Semantic Cache + Circuit Breaker + Temporal workflows

## Orchestration & Mediator Layers
- **Mediator Layer**: Agentive request analysis, Command Query Responsibility Segregation (CQRS), Saga patterns, and Transactional Outbox for distributed consistency.
- **Exposed Services**: MCP, standard REST API, WSDL for Legacy Banking API integrations.
- **Storage Strategy**: Golden CDC source replicated to a Silver Copy to support CQRS for Read Services.

## Integration & Legacy Layers
- **Anti-Corruption Layer (ACL)**: Spring Boot + MuleSoft wrapping legacy endpoints.
- **BackEnd Systems**: 
  - FinOne (Customer API)
  - Finacle (Core Banking API - Legacy)
