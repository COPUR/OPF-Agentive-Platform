import React, { useState } from 'react';

export function TppOnboarding() {
  const [step, setStep] = useState(1);

  const handleSimulate = () => {
    setStep(2);
    setTimeout(() => setStep(3), 3000);
  };

  return (
    <div>
      <div className="page-header">
        <h1>TPP Admission Portal</h1>
        <p>Submit your enterprise documentation. Our Agentive Security LLMs will process your application autonomously.</p>
      </div>

      <div className="glass-card" style={{ maxWidth: '600px', margin: '0 auto' }}>
        {step === 1 && (
          <div style={{ textAlign: 'center' }}>
            <h3>Upload Documentation</h3>
            <p style={{ color: 'var(--text-secondary)', margin: '16px 0' }}>
              Please provide your Trade License, AML Policy, and Technical Architecture.
            </p>
            <button className="btn-primary" onClick={handleSimulate}>Submit for Agent Review</button>
          </div>
        )}

        {step === 2 && (
          <div style={{ textAlign: 'center' }}>
            <h3 className="pulsing" style={{ color: 'var(--accent-cyan)' }}>Agent Processing...</h3>
            <p style={{ color: 'var(--text-secondary)', margin: '16px 0' }}>
              The Security vLLM is scanning your documents for DLP and Risk Compliance.
              <br/>This workflow is durably executed via Temporal.
            </p>
            <div style={{ height: '4px', background: 'var(--bg-glass)', borderRadius: '2px', marginTop: '24px', overflow: 'hidden' }}>
              <div style={{ width: '50%', height: '100%', background: 'var(--accent-cyan)', animation: 'pulse 1s infinite' }}></div>
            </div>
          </div>
        )}

        {step === 3 && (
          <div style={{ textAlign: 'center' }}>
            <h3 style={{ color: '#00ff88' }}>Admission Approved</h3>
            <p style={{ color: 'var(--text-secondary)', margin: '16px 0' }}>
              The Agentive workflow has verified your documents and autonomously provisioned your Keycloak IAM credentials.
            </p>
            <div style={{ background: 'rgba(0, 255, 136, 0.1)', padding: '16px', borderRadius: '8px', border: '1px solid rgba(0, 255, 136, 0.3)', marginBottom: '16px' }}>
              <strong style={{ color: '#00ff88' }}>Client ID:</strong> nebras-tpp-84920
            </div>
            <button className="btn-primary" onClick={() => setStep(1)}>Reset Simulation</button>
          </div>
        )}
      </div>
    </div>
  );
}
