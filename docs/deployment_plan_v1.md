# Deployment Plan
**Project:** OPF-Agentive-Platform

## 1. GitOps Strategy
We use ArgoCD for continuous delivery to AWS EKS clusters.
- **Environments:** DEV -> UAT -> PRE-PROD -> PROD.

## 2. Phased Rollout (Strangler Fig)
- **Phase 1:** Deploy the Cognitive Layer and Identity Access purely in "Shadow Mode" (reading traffic but not mutating).
- **Phase 2:** Reroute 10% of API portal traffic to the Mediator.
- **Phase 3:** Full cutover for Open Finance APIs. Legacy monolith read endpoints are deprecated.

## 3. Infrastructure as Code
All AWS EKS configurations, MSK (Kafka) clusters, and PostgreSQL RDS instances are provisioned via Terraform modules checked into the `infrastructure/` monorepo.

## 4. Local Sandbox Environment (OpenShift/Istio)
For local development, we maintain a complete OpenShift Sandbox ecosystem via CodeReady Containers (CRC) or a local K3s instance mapped to Istio. This ensures that the Zero-Trust mesh policies developed locally behave identically in AWS EKS.

![Local Sandbox Topology](diagrams/deployment_sandbox_v1.svg)

**To spin up the sandbox:**
1. Navigate to `infrastructure/sandbox/`
2. Ensure you are authenticated with your cluster (`oc login`).
3. Execute `./start-sandbox.sh`
4. This script applies all Istio Gateway, VirtualService, and Deployment manifests to the `opf-sandbox` namespace. It automatically injects the Envoy sidecars, boots Temporal, Keycloak, Kafka, Redis, and a `pgvector` Postgres instance pre-seeded with dummy Open Finance semantic vectors.
