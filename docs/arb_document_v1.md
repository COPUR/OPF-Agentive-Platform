# Architecture Review Board (ARB) Submission
**Project:** OPF-Agentive-Platform

## 1. Problem Statement
Legacy banking interfaces are too tightly coupled to satisfy immediate OpenFinance capabilities required by UAE Central Bank regulations.

## 2. Proposed Architecture
![Container Diagram](diagrams/c4_container_v1.svg)

## 3. Trade-offs & Tech Debts
- **Tech Debt:** Maintaining duplicate data in the Silver Copy DB.
- **Mitigation:** Strict CDC (Change Data Capture) polling via Kafka.
- **Trade-off:** High operational complexity using Temporal and Kafka together, offset by extreme reliability during system failures.

## 4. Compliance & Security Gates
- All requests mandate DPoP bound OAuth2 tokens.
- PII is automatically hashed via Keycloak/MongoDB schemas.
- pgvector ensures agentic RAG outputs comply with PCI-DSS.
