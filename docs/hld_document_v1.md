# High-Level Design (HLD)
**Project:** OPF-Agentive-Platform

## 1. Executive Summary
The OPF-Agentive-Platform translates legacy core banking requests into unified Open Finance CBUAE standards.

## 2. Architecture Context
![System Context](diagrams/c4_context_v1.svg)

## 3. High-Level Components
- **API Gateway (Kong):** Secures all incoming developer traffic.
- **Cognitive Layer (Temporal):** Orchestrates AI intent mapping.
- **Mediator (CQRS/Saga):** Enforces distributed transaction state.
- **Legacy Systems:** Finacle & FinOne core integration via ACL.

## 4. Key Architectural Decisions
- Use of localized LLMs to maintain Data Sovereignty.
- Bounded context design avoiding "Big Bang" monolith refactoring.
