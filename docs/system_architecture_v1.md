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
