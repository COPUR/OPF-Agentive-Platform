# Change Advisory Board (CAB) Document
**Project:** OPF-Agentive-Platform

## 1. Change Summary
Deployment of the v1 Open Finance API Gateway and Mediator layer.

## 2. Deployment Risks
- **Risk 1:** Heavy initial load on legacy Finacle APIs during cache warming.
- **Mitigation:** Rate limiters applied at the Kong API Gateway layer.
- **Risk 2:** Network partition between the Mediator and Kafka Outbox.
- **Mitigation:** Fallback to local Postgres transactional persistence until Kafka heals.

## 3. Rollback Plan
- Utilizing GitOps (ArgoCD), any failure triggers an immediate automated revert to the previous verified Helm chart state.
- No direct database migrations (schema changes) will be executed in this phase to allow instant pod rollbacks without data corruption.
