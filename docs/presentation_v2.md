---
marp: true
theme: default
class: lead
---

# OPF-Agentive-Platform
**Enterprise Architecture Deck**
*Modernizing BaaS for Open Finance CBUAE Compliance*

---

## The Challenge
- Legacy Core Banking APIs (Finacle, FinOne) are tightly coupled.
- Unable to swiftly meet UAE Central Bank Open Finance regulations.
- Security boundaries are monolithic.

---

## The Solution: Agentive Mediator
- Introduce a **Cognitive Layer** utilizing Agentive Workflows (Temporal).
- Use a **Saga / CQRS Pattern** to orchestrate legacy calls safely.
- Expose modern, unified JSON endpoints via **Kong Gateway**.
- Manage identity & party data using **Keycloak + MongoDB**.

---

## Architecture Overview
![h:400](diagrams/C4_Container_v2.svg)

---

## Business Value & Productivity
- **Decoupled Domains:** Eliminates "Big Bang" migration risks.
- **AI Productivity Tracking:** DORA metrics collected autonomously in a psychologically safe, anonymized manner.
- **Compliance as Code:** Auto-remediation filters inject sanitization classes at build-time.

---

## Next Steps & Deployment
1. **Shadow Mode:** Deploy Cognitive Layer to observe traffic.
2. **Strangler Fig Routing:** Gateway diverts 10% traffic to Mediator.
3. **Full Cutover:** Deprecate legacy read endpoints.

---
**Thank You**
*Architecture Review Board Submission*
