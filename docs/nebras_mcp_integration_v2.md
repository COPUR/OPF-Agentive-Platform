# Nebras MCP Integration Guide

The **Model Context Protocol (MCP)** allows you to seamlessly expose the Nebras Open Finance Platform to AI Agents. By running this server, agents like Claude Desktop can autonomously evaluate loan criteria, generate SCA Consent URLs, and execute Salary Batches!

## 1. Installation & Build

Ensure you have Node.js (v18+) installed.

```bash
cd opf-nebras-mcp-server
npm install
npm run build
```

## 2. Configuring Claude Desktop

To allow Claude (or any MCP-compliant agent) to use the Open Finance tools, you must register the local server in your Claude Desktop configuration file.

### Location of config:
- **macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
- **Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

### Add the Server:
```json
{
  "mcpServers": {
    "nebras-openfinance": {
      "command": "node",
      "args": [
        "/absolute/path/to/opf-nebras-mcp-server/dist/index.js"
      ]
    }
  }
}
```

## 3. Available AI Tools

Once configured, the AI Agent will automatically have access to:
- **`nebras_initiate_consent`**: The AI can generate secure UAE Pass authorization URLs.
- **`nebras_execute_salary_batch`**: The AI can execute large payroll transactions asynchronously.
- **`nebras_execute_car_lease`**: The AI can process individual car loan payments.

### Example Prompt to AI
> "We need to run the monthly payroll for Corporate Account CORP_12345. There are 50 employees and the total is 250,000 AED. Please generate the consent link first, and once I approve it, execute the batch."

The AI will handle the sequential orchestration of the MCP tools automatically!

## 4. Agent Economics & Temporal Coordination
When Claude invokes these MCP tools, the requests are routed through the `FteHarnessCoordinator`.
- **Economics**: Every MCP tool execution consumes token budgets monitored by the `FteCostOptimizer`. If an external LLM attempts to spam or loop over an MCP tool, the system will throw an `InsufficientFteBudgetException`, protecting the backend from runaway costs.
- **State Management**: The MCP session history is durably logged in the `MemoryBank` via Temporal, allowing multi-turn conversational persistence even if the local Claude Desktop client restarts.
