# Data Architecture
**Project:** OPF-Agentive-Platform
**Version:** v1

This document defines the data topologies, persistence layers, and flow of state throughout the Agentive Open Finance Platform.

## 1. Topologies & Persistence Layers

### 1.1 The "Memory Banks" (Agent State & Economics)
- **Engine**: MongoDB (Document).
- **Purpose**: Replaces traditional static read/write models with active Memory Banks (`MongoMemoryBank.java`). This layer stores the `SessionMemory` enabling long-running Temporal AI workflows to recall past user actions across WebSocket reconnects. It also persistently tracks LLM token consumption (`AgentFteData`) for the `FteCostOptimizer`.
- **Synchronization**: Updated asynchronously via Kafka when transactions clear, or upon explicit Temporal workflow polling.

### 1.2 The Semantic Cache
- **Engine**: PostgreSQL extended with `pgvector`.
- **Purpose**: Stores vector embeddings of Natural Language intents. 
- **Stored XSS & Context Poisoning Prevention**: To prevent malicious payloads from poisoning the vector space and manipulating future agent decisions, all data must pass through the **AI Security Layer**. The internal Security LLM runs Context Poisoning checks and trust scoring before insertion.
- **Flow**: When a request hits the Cognitive Layer, it is vectorized. If the Cosine Similarity against the cache is > 0.95, the workflow instantly executes the mapped intent rather than invoking the LLM.

### 1.3 Identity & Party Data
- **Engine**: MongoDB (Document).
- **Purpose**: Stores unstructured and rapidly evolving party profiles, SCA consent objects, and Keycloak realm definitions.

### 1.4 Temporal State
- **Engine**: PostgreSQL.
- **Purpose**: Temporal automatically maintains the complete event history of every AI workflow. If a pod dies mid-reasoning, Temporal reinstantiates the workflow on another pod using this event sourcing DB.

## 2. CQRS & Event Sourcing
The platform strictly separates Read commands from Write operations using CQRS (Command Query Responsibility Segregation).

- **Queries (Reads)**: Hitting `GET /accounts` routes through the Gateway -> Cognitive Semantic Match -> Mediator Silver Copy. This is a fast, synchronous read that never touches the legacy core.
- **Commands (Writes)**: Hitting `POST /domestic-payments` routes to the Cognitive Layer. The LLM validates the intent and triggers the Mediator. The Mediator uses the **Transactional Outbox Pattern**:
  1. It commits a `PAYMENT_PENDING` state to its local PostgreSQL DB.
  2. In the *same transaction*, it writes an event to a Kafka `outbox` table.
  3. A CDC (Change Data Capture) tool or polling publisher pushes this to the Kafka Broker.
  4. The ACL picks up the event, translates it, and executes the SOAP call on the mainframe.

## 3. Data Sovereignty & PII Handling
Because AI models are non-deterministic, raw PII (Personally Identifiable Information) must never enter the prompt context window.
- **The Harness Layer**: Before any prompt is sent to the local Llama-3 model, a Java interface interceptor (`Agent3Scoper`) scans the payload.
- **Hashing**: Account Numbers, Emirates IDs (EID), and exact financial values are hashed using SHA-256 or replaced with tokenized variables (e.g., `[ACCOUNT_A]`). 
- **Reconstruction**: Once the LLM returns the logical intent (e.g., `EXECUTE_TRANSFER(source=[ACCOUNT_A])`), the deterministic Java layer reconstructs the actual payload using the tokens before sending it to the Mediator.
