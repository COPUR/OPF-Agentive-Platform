#!/bin/bash
# start-sandbox.sh
# Bootstraps the local OPF Sandbox Environment

export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"
echo "🚀 Starting OPF Agentive Platform Sandbox on OpenShift/Istio..."

# Apply manifests
oc apply -f ../openshift/01-namespace-mesh.yaml
oc apply -f ../openshift/

echo "⏳ Waiting for pods to initialize (this may take a few minutes)..."
oc wait --for=condition=ready pod -l app=postgres-vector -n opf-sandbox --timeout=120s
oc wait --for=condition=ready pod -l app=temporal-server -n opf-sandbox --timeout=120s

# Execute the embedding initialization script
echo "🧠 Seeding Semantic Cache..."
chmod +x init-embeddings.sh
./init-embeddings.sh

echo "✅ OpenShift Sandbox is successfully running!"
echo "----------------------------------------------------"
echo "Temporal UI:        http://localhost:8080"
echo "Keycloak Admin:     http://localhost:8081"
echo "Kafka Broker:       localhost:9092"
echo "Postgres Vector:    localhost:5432"
echo "Redis Cache:        localhost:6379"
echo "MongoDB:            localhost:27017"
echo "----------------------------------------------------"
