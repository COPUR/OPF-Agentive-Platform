import React from 'react';

export function ApiCatalog() {
  return (
    <div>
      <div className="page-header">
        <h1>API Catalog</h1>
        <p>Discover Agentive Banking Intents available on the Nebras network.</p>
      </div>

      <div className="grid-2">
        <div className="glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <h3>DOMESTIC_PAYMENT</h3>
            <span className="badge active">Live</span>
          </div>
          <h4 style={{ color: 'var(--accent-cyan)', marginTop: '4px' }}>Type: SALARY_BATCH</h4>
          <p style={{ marginTop: '16px', color: 'var(--text-secondary)' }}>
            Execute bulk corporate payrolls. Automatically invokes the Reasoning LLM for anomaly detection and PII scrubbing before routing to legacy Finacle.
          </p>
          <div style={{ marginTop: '24px', padding: '12px', background: 'rgba(0,0,0,0.3)', borderRadius: '8px', fontFamily: 'monospace', color: '#9ca3af' }}>
            POST /domestic-payments
          </div>
        </div>

        <div className="glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <h3>DOMESTIC_PAYMENT</h3>
            <span className="badge active">Live</span>
          </div>
          <h4 style={{ color: 'var(--accent-purple)', marginTop: '4px' }}>Type: CAR_LEASE</h4>
          <p style={{ marginTop: '16px', color: 'var(--text-secondary)' }}>
            Execute recurring car loan payments. Triggers a Strong Customer Authentication (SCA) Consent redirect to UAE Pass for the end-user.
          </p>
          <div style={{ marginTop: '24px', padding: '12px', background: 'rgba(0,0,0,0.3)', borderRadius: '8px', fontFamily: 'monospace', color: '#9ca3af' }}>
            POST /domestic-payments
          </div>
        </div>
      </div>
    </div>
  );
}
