"use client";

import { useState, useEffect } from "react";

export default function DeveloperPortal() {
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [chatInput, setChatInput] = useState("");
  const [chatMessages, setChatMessages] = useState([
    {
      id: "1",
      sender: "agent",
      text: "Hello! I am the OPF Integration Agent. How can I help you build with the X-Bank API today?",
    },
  ]);
  const [ws, setWs] = useState<WebSocket | null>(null);

  useEffect(() => {
    if (!isChatOpen) return;

    const socket = new WebSocket("ws://localhost:8080/ws/agent-fte");

    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (data.type === "AGENT_RESPONSE" || data.type === "ERROR") {
          setChatMessages((prev) => [
            ...prev,
            {
              id: Date.now().toString(),
              sender: "agent",
              text: data.message,
            },
          ]);
        }
      } catch (err) {
        console.error("Failed to parse message", err);
      }
    };

    setWs(socket);

    return () => {
      socket.close();
    };
  }, [isChatOpen]);

  const handleChatSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!chatInput.trim() || !ws || ws.readyState !== WebSocket.OPEN) return;

    setChatMessages((prev) => [
      ...prev,
      { id: Date.now().toString(), sender: "user", text: chatInput },
    ]);
    
    ws.send(
      JSON.stringify({
        prompt: chatInput,
        role: "TOPOLOGY_SYNTHESIS", // Use architecture/integration expert
      })
    );
    
    setChatInput("");
  };

  return (
    <main className="min-h-screen relative flex flex-col">
      {/* Navigation */}
      <nav className="glass border-b border-white/5 sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-emerald-500 rounded-lg flex items-center justify-center">
              <span className="font-bold text-white tracking-tighter">XB</span>
            </div>
            <span className="text-xl font-semibold tracking-tight text-white">
              OpenFinance Portal
            </span>
          </div>
          <div className="flex items-center gap-6 text-sm font-medium text-slate-300">
            <a href="#" className="hover:text-emerald-400 transition-colors">Documentation</a>
            <a href="#" className="hover:text-emerald-400 transition-colors">API Reference</a>
            <a href="#" className="hover:text-emerald-400 transition-colors">SDKs</a>
            <button className="px-5 py-2.5 rounded-full bg-white text-black hover:bg-emerald-50 transition-colors font-semibold">
              Sign In (TPP)
            </button>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="flex-1 flex items-center justify-center py-24 px-6 text-center">
        <div className="max-w-4xl mx-auto flex flex-col items-center gap-8">
          <h1 className="text-5xl md:text-7xl font-bold tracking-tight text-transparent bg-clip-text bg-gradient-to-br from-white to-slate-400 leading-tight">
            Build the Future of <br className="hidden md:block" />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-emerald-600">
              Banking in the UAE
            </span>
          </h1>
          <p className="text-lg md:text-xl text-slate-400 max-w-2xl leading-relaxed">
            Integrate with X-Bank's CBUAE-compliant OpenFinance APIs. Access robust SDKs, real-time webhooks, and agent-assisted integration support.
          </p>
          <div className="flex items-center gap-4 mt-4">
            <button className="px-8 py-4 rounded-full bg-emerald-500 hover:bg-emerald-400 transition-colors font-semibold text-white shadow-lg shadow-emerald-500/20">
              Get API Keys
            </button>
            <button className="px-8 py-4 rounded-full glass hover:bg-white/10 transition-colors font-semibold text-white">
              Read Docs
            </button>
          </div>
        </div>
      </section>

      {/* Floating Chat Widget */}
      <div className="fixed bottom-6 right-6 z-50 flex flex-col items-end">
        {isChatOpen && (
          <div className="glass w-80 md:w-96 h-[500px] mb-4 rounded-2xl flex flex-col overflow-hidden shadow-2xl shadow-emerald-500/10 border border-emerald-500/20">
            <div className="bg-emerald-500/10 px-4 py-3 border-b border-emerald-500/20 flex justify-between items-center">
              <div>
                <h3 className="font-semibold text-emerald-400">Agent-FTE Support</h3>
                <p className="text-xs text-slate-400">Online & Ready</p>
              </div>
              <button 
                onClick={() => setIsChatOpen(false)}
                className="text-slate-400 hover:text-white"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                </svg>
              </button>
            </div>
            
            <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-4">
              {chatMessages.map((msg) => (
                <div key={msg.id} className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"}`}>
                  <div className={`max-w-[85%] p-3 rounded-2xl text-sm ${
                    msg.sender === "user" ? "bg-emerald-600 text-white rounded-br-none" : "bg-slate-800 text-slate-200 rounded-bl-none border border-slate-700"
                  }`}>
                    {msg.text}
                  </div>
                </div>
              ))}
            </div>

            <form onSubmit={handleChatSubmit} className="p-3 border-t border-white/10 bg-slate-900/50">
              <input
                type="text"
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                placeholder="Ask for integration help..."
                className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all text-white placeholder:text-slate-500"
              />
            </form>
          </div>
        )}
        
        <button
          onClick={() => setIsChatOpen(!isChatOpen)}
          className="w-14 h-14 bg-emerald-500 hover:bg-emerald-400 text-white rounded-full flex items-center justify-center shadow-lg shadow-emerald-500/30 transition-transform hover:scale-105 active:scale-95"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-6 h-6">
            <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 12.76c0 1.6 1.123 2.994 2.707 3.227 1.087.16 2.185.283 3.293.369V21l4.076-4.076a1.526 1.526 0 011.037-.443 48.282 48.282 0 005.68-.494c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0012 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018z" />
          </svg>
        </button>
      </div>
    </main>
  );
}
