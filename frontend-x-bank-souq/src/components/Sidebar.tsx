import React from 'react';

interface SidebarProps {
  currentView: string;
  setView: (view: string) => void;
}

export function Sidebar({ currentView, setView }: SidebarProps) {
  return (
    <div className="sidebar">
      <div className="logo">NEBRAS API SOUQ</div>
      <div className="nav-links">
        <div 
          className={`nav-item ${currentView === 'dashboard' ? 'active' : ''}`}
          onClick={() => setView('dashboard')}
        >
          Dashboard
        </div>
        <div 
          className={`nav-item ${currentView === 'catalog' ? 'active' : ''}`}
          onClick={() => setView('catalog')}
        >
          API Catalog
        </div>
        <div 
          className={`nav-item ${currentView === 'onboarding' ? 'active' : ''}`}
          onClick={() => setView('onboarding')}
        >
          TPP Admission
        </div>
      </div>
    </div>
  );
}
