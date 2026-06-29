# Technology Architecture
**Project:** OPF-Agentive-Platform
**Version:** v1

This document outlines the concrete technology stack powering the Agentive Open Finance Platform, designed for extreme scalability, data sovereignty, and zero-trust security.

## 1. Deployment Platform & Infrastructure
- **Orchestration**: Red Hat OpenShift acting as a **Cognitive Orchestration Layer** (Context Kubernetes).
- **Service Mesh**: Istio. All pods are injected with Envoy proxy sidecars. This guarantees mTLS and enforces Zero-Trust routing policies.
- **Strict Sandboxing**: Autonomous agents executing code are isolated in strict sandboxed environments (**WASM** or **gVisor**) to mitigate cyber risks; standard Docker boundaries are insufficient.
- **The Kill Switch**: A deterministic hardware-level kill switch is wired to the Istio Mesh. Upon anomaly detection, it automatically revokes the agent's SPIFFE ID and scales the pod down to zero.

## 2. Application Layer (Frontend & Backend)
- **Developer Portal Frontend**: React 19, Vite, and Vanilla CSS with a Cyber-Corporate dark mode aesthetic (`frontend-x-bank-souq`).
- **Gateway & API Management**: Spring Cloud Gateway, enabling reactive non-blocking I/O.
- **Microservices Core**: Java 17+, Spring Boot 3.x.
- **Dependency Management**: Maven.
- **Security Scoping**: Custom Java components inject security policies during the build phase and hash PII before it reaches the AI models.

## 3. Cognitive & AI Integration
- **Framework**: Model Context Protocol (MCP) SDK, LangChain4j, Temporal SDK.
- **Models**: Local `vLLM` clusters for Security (DLP) and Llama-3-70b for Intent Analysis. Node.js MCP server exposes tools.
- **Reasoning & Agent Workflows**: Temporal handles durable execution (`TppAdmissionWorkflow`), allowing LLM inferences to fail, pause, or wait for Human-in-the-Loop review without breaking the process. Simple requests use ultra-fast Small Language Models (SLMs) before waking up foundational LLMs (e.g., Llama-3-70B), significantly optimizing HBM bandwidth. A `CognitiveCircuitBreaker` guards LLM calls with deterministic fallbacks when models are unresponsive.
- **Semantic Caching**: PostgreSQL extended with `pgvector` drastically reduces Time-to-First-Token (TTFT) by fetching identical intent embeddings directly from memory.

## 4. Mediator & Async Streaming
- **Event Broker**: Confluent Kafka. Used extensively for the Outbox Pattern. When the Cognitive Layer decides an action must occur on the legacy mainframe, the Mediator commits the intent to a local DB and fires an event to Kafka.
- **Anti-Corruption Translation**: Kafka listeners inside the ACL consume JSON events and map them strictly into SOAP XML for the legacy Finacle core.

## 5. Developer Portal (Frontend)
- **Framework**: React 18+ bundled with Vite.
- **Styling**: TailwindCSS.
- **Integration**: Communicates strictly with the API Gateway via OAuth2/OIDC flows, managing SCA (Strong Customer Authentication) redirects.
