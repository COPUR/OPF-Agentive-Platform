import React from 'react';

export function Dashboard() {
  return (
    <div>
      <div className="page-header">
        <h1>Developer Dashboard</h1>
        <p>Monitor your Open Finance Integrations and Agentive workflows.</p>
      </div>

      <div className="grid-3" style={{ marginBottom: '40px' }}>
        <div className="glass-card">
          <h3 style={{ color: 'var(--text-secondary)' }}>Active APIs</h3>
          <div style={{ fontSize: '36px', color: 'var(--accent-cyan)' }}>2</div>
        </div>
        <div className="glass-card">
          <h3 style={{ color: 'var(--text-secondary)' }}>Consent Status</h3>
          <div style={{ fontSize: '36px', color: '#ffaa00' }}>3 Pending</div>
        </div>
        <div className="glass-card">
          <h3 style={{ color: 'var(--text-secondary)' }}>Agentive Calls (24h)</h3>
          <div style={{ fontSize: '36px', color: 'var(--accent-purple)' }}>14,205</div>
        </div>
      </div>

      <h2>Available Integrations</h2>
      <div className="grid-2">
        <div className="glass-card">
          <h3>Nebras Java SDK</h3>
          <p style={{ marginTop: '8px', color: 'var(--text-secondary)' }}>
            DPoP Cryptography & Consent Tokenization abstracted.
          </p>
          <button className="btn-primary" style={{ marginTop: '16px' }}>Download SDK</button>
        </div>
        
        <div className="glass-card">
          <h3>Nebras MCP Server</h3>
          <p style={{ marginTop: '8px', color: 'var(--text-secondary)' }}>
            Expose Open Finance to your local AI LLM Agents.
          </p>
          <button className="btn-primary" style={{ marginTop: '16px', background: 'var(--accent-cyan)', color: 'black' }}>
            View Configuration
          </button>
        </div>
      </div>
    </div>
  );
}
