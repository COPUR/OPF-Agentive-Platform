import { useState } from 'react';
import { Sidebar } from './components/Sidebar';
import { Dashboard } from './components/Dashboard';
import { ApiCatalog } from './components/ApiCatalog';
import { TppOnboarding } from './components/TppOnboarding';

function App() {
  const [currentView, setView] = useState('dashboard');

  return (
    <div className="app-container">
      <Sidebar currentView={currentView} setView={setView} />
      <main className="main-content">
        {currentView === 'dashboard' && <Dashboard />}
        {currentView === 'catalog' && <ApiCatalog />}
        {currentView === 'onboarding' && <TppOnboarding />}
      </main>
    </div>
  );
}

export default App;
