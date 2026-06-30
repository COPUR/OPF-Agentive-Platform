# Low-Level Design (LLD)
**Project:** OPF-Agentive-Platform

## 1. Component Interactions
![Component Diagram](diagrams/C4_Component.svg)

> **Deep Dive:** For an exhaustive breakdown of the specific Open Finance API Endpoints, Webhook configurations, and the Tripartite AI Architecture (Harness + Cognitive + Model) powering these interactions, please refer to the [API Definitions & AI Anatomy](api_and_ai_anatomy_v1.md) documentation.

## 2. Sequence Workflows
The following describes the exact routing paths for API requests:
![Sequence Diagram](diagrams/Sequence_Diagram.svg)

## 3. State Management & Saga
To prevent data inconsistency when legacy APIs fail, a Compensating Transaction model is used.
![Saga State Machine](diagrams/State_Machine.svg)

## 4. Database Schema (Silver Copy)
- `party_data`: Stores SCA profiles securely on MongoDB.
- `transaction_outbox`: Stores un-synced events locally in Postgres before Kafka publishing.
