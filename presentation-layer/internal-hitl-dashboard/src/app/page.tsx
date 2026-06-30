"use client";

import { useState, useRef, useEffect } from "react";

type Message = {
  id: string;
  sender: "user" | "agent";
  text: string;
  agentRole?: string;
};

export default function HITLDashboard() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      sender: "agent",
      text: "Welcome to the OPF Command Center. I am the Architecture Agent-FTE. Please upload your PRD or ask me a question about the current sprint requirements.",
      agentRole: "Architecture",
    },
  ]);
  const [input, setInput] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [activeAgent, setActiveAgent] = useState("Architecture");
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const [ws, setWs] = useState<WebSocket | null>(null);
  const [connectionStatus, setConnectionStatus] = useState("Disconnected");

  const agents = [
    "Autonomous Ingestion",
    "Topology Synthesis",
    "Compliance Validation",
    "Stakeholder Signoff",
    "Architecture",
    "Code Audit Telemetry"
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isTyping]);

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/ws/agent-fte");

    socket.onopen = () => {
      setConnectionStatus("Connected");
      console.log("WebSocket connected");
    };

    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (data.type === "STATUS") {
          setIsTyping(true);
        } else if (data.type === "AGENT_RESPONSE" || data.type === "ERROR") {
          setIsTyping(false);
          const agentMsg: Message = {
            id: Date.now().toString(),
            sender: "agent",
            text: data.message,
            agentRole: data.role || activeAgent,
          };
          setMessages((prev) => [...prev, agentMsg]);
        }
      } catch (err) {
        console.error("Failed to parse message", err);
      }
    };

    socket.onclose = () => {
      setConnectionStatus("Disconnected");
      console.log("WebSocket disconnected");
    };

    setWs(socket);

    return () => {
      socket.close();
    };
  }, [activeAgent]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || !ws || ws.readyState !== WebSocket.OPEN) return;

    const userMsg: Message = {
      id: Date.now().toString(),
      sender: "user",
      text: input,
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    
    // Convert friendly name to Enum format (e.g. "Compliance Validation" -> "COMPLIANCE_VALIDATION")
    const formattedRole = activeAgent.toUpperCase().replace(" ", "_");

    ws.send(
      JSON.stringify({
        prompt: input,
        role: formattedRole,
      })
    );
  };

  return (
    <main className="min-h-screen p-8 flex flex-col items-center">
      <header className="w-full max-w-6xl mb-8 flex justify-between items-center">
        <div>
          <h1 className="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-500">
            X-Bank OPF Command Center
          </h1>
          <p className="text-slate-400 mt-2">
            Internal HITL Dashboard & PRD Interview Interface
          </p>
        </div>
        <div className="flex gap-4">
          <button className="px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-500 transition-colors font-medium">
            Upload PRD (Dropzone)
          </button>
        </div>
      </header>

      <div className="w-full max-w-6xl grid grid-cols-1 md:grid-cols-4 gap-8 flex-1">
        {/* Sidebar */}
        <div className="glass-panel rounded-2xl p-6 flex flex-col gap-4">
          <h2 className="text-xl font-semibold mb-4 text-slate-200">
            Active Agent-FTEs
          </h2>
          {agents.map((agent) => (
            <button
              key={agent}
              onClick={() => setActiveAgent(agent)}
              className={`text-left px-4 py-3 rounded-lg transition-all ${
                activeAgent === agent
                  ? "bg-blue-600/20 border border-blue-500/50 text-blue-300"
                  : "hover:bg-slate-800 text-slate-400"
              }`}
            >
              <div className="flex items-center gap-3">
                <div
                  className={`w-2 h-2 rounded-full ${
                    activeAgent === agent ? "bg-blue-400 animate-pulse" : "bg-slate-600"
                  }`}
                />
                {agent}
              </div>
            </button>
          ))}
        </div>

        {/* Chat Area */}
        <div className="glass-panel rounded-2xl md:col-span-3 flex flex-col overflow-hidden glow-border">
          <div className="bg-slate-800/50 px-6 py-4 border-b border-slate-700/50">
            <h3 className="font-medium text-lg">Interviewing: {activeAgent}</h3>
            <p className="text-sm text-slate-400">WebSocket connection: Stable</p>
          </div>

          <div className="flex-1 p-6 overflow-y-auto flex flex-col gap-6">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex ${
                  msg.sender === "user" ? "justify-end" : "justify-start"
                }`}
              >
                <div
                  className={`max-w-[80%] rounded-2xl px-6 py-4 ${
                    msg.sender === "user"
                      ? "bg-blue-600 text-white rounded-br-none"
                      : "bg-slate-800 text-slate-200 rounded-bl-none border border-slate-700"
                  }`}
                >
                  {msg.sender === "agent" && (
                    <div className="text-xs font-semibold text-blue-400 mb-2 tracking-wider uppercase">
                      {msg.agentRole}
                    </div>
                  )}
                  <p className="leading-relaxed">{msg.text}</p>
                </div>
              </div>
            ))}
            {isTyping && (
              <div className="flex justify-start">
                <div className="bg-slate-800 border border-slate-700 rounded-2xl rounded-bl-none px-6 py-4 flex gap-2 items-center">
                  <div className="w-2 h-2 bg-blue-400 rounded-full animate-bounce" />
                  <div className="w-2 h-2 bg-blue-400 rounded-full animate-bounce delay-75" />
                  <div className="w-2 h-2 bg-blue-400 rounded-full animate-bounce delay-150" />
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          <form
            onSubmit={handleSend}
            className="p-4 bg-slate-800/30 border-t border-slate-700/50"
          >
            <div className="relative">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder={`Ask the ${activeAgent} agent...`}
                className="w-full bg-slate-900/50 border border-slate-700 rounded-xl pl-6 pr-16 py-4 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all"
              />
              <button
                type="submit"
                disabled={!input.trim()}
                className="absolute right-2 top-2 bottom-2 aspect-square bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:hover:bg-blue-600 rounded-lg flex items-center justify-center transition-colors"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                  className="w-5 h-5"
                >
                  <path d="M3.478 2.404a.75.75 0 0 0-.926.941l2.432 7.905H13.5a.75.75 0 0 1 0 1.5H4.984l-2.432 7.905a.75.75 0 0 0 .926.94 60.519 60.519 0 0 0 18.445-8.986.75.75 0 0 0 0-1.218A60.517 60.517 0 0 0 3.478 2.404Z" />
                </svg>
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}
