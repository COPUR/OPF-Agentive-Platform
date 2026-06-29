#!/bin/bash
# init-embeddings.sh
# Initializes the pgvector semantic cache with dummy mock data for the Sandbox.

export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"

echo "Waiting for postgres-vector to be ready..."
sleep 10

echo "Initializing pgvector and semantic cache tables..."

export PGPASSWORD=opfpassword

# Get the postgres pod name
PG_POD=$(oc get pod -n opf-sandbox -l app=postgres-vector -o jsonpath="{.items[0].metadata.name}")

# Create Extension and Table
oc exec -n opf-sandbox -it $PG_POD -- psql -U opfuser -d semantic_cache -c "CREATE EXTENSION IF NOT EXISTS vector;"
oc exec -n opf-sandbox -it $PG_POD -- psql -U opfuser -d semantic_cache -c "
CREATE TABLE IF NOT EXISTS semantic_intents (
    id SERIAL PRIMARY KEY,
    intent_name VARCHAR(100),
    intent_description TEXT,
    embedding vector(3) -- Using a mock 3-dimensional vector for local testing instead of full 1536d
);
"

# Insert mock vectors for fast local matching
echo "Inserting mock intent vectors..."
oc exec -n opf-sandbox -it $PG_POD -- psql -U opfuser -d semantic_cache -c "
INSERT INTO semantic_intents (intent_name, intent_description, embedding) VALUES
('GET_ACCOUNTS', 'Retrieve the list of authorized bank accounts', '[0.9, 0.1, 0.0]'),
('GET_BALANCES', 'Retrieve the real-time balance for an account', '[0.8, 0.2, 0.0]'),
('INITIATE_PAYMENT', 'Initialize a domestic PISP payment consent', '[0.1, 0.9, 0.0]'),
('REVOKE_CONSENT', 'Revoke an existing Open Finance consent', '[0.0, 0.1, 0.9]');
"

echo "Vector semantic cache initialized successfully!"
