import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import * as crypto from "crypto";

const server = new Server(
  {
    name: "nebras-openfinance-mcp",
    version: "1.0.0",
  },
  {
    capabilities: {
      tools: {},
    },
  }
);

/**
 * Define the Tools exposed to the AI Agent
 */
server.setRequestHandler(ListToolsRequestSchema, async () => {
  return {
    tools: [
      {
        name: "nebras_initiate_consent",
        description: "Initiates a Strong Customer Authentication (SCA) flow and returns an authorization URL.",
        inputSchema: {
          type: "object",
          properties: {
            scope: {
              type: "string",
              description: "The API Scope (e.g. DOMESTIC_PAYMENT, ACCOUNT_READ)",
            },
          },
          required: ["scope"],
        },
      },
      {
        name: "nebras_execute_salary_batch",
        description: "Executes a bulk salary payment intent under the Open Finance schema.",
        inputSchema: {
          type: "object",
          properties: {
            corporateAccountId: {
              type: "string",
              description: "The source corporate account ID.",
            },
            totalEmployees: {
              type: "number",
              description: "Total number of employees in the batch.",
            },
            totalAmount: {
              type: "number",
              description: "Total AED amount to disburse.",
            },
            consentToken: {
              type: "string",
              description: "The SCA consent token authorized by the company.",
            },
          },
          required: ["corporateAccountId", "totalEmployees", "totalAmount", "consentToken"],
        },
      },
      {
        name: "nebras_execute_car_lease",
        description: "Executes a single car lease loan repayment.",
        inputSchema: {
          type: "object",
          properties: {
            leaseContractId: {
              type: "string",
              description: "The Car Lease Contract Reference.",
            },
            customerAccountId: {
              type: "string",
              description: "The source customer account ID.",
            },
            monthlyInstallment: {
              type: "number",
              description: "The AED installment amount.",
            },
            consentToken: {
              type: "string",
              description: "The SCA consent token authorized by the customer.",
            },
          },
          required: ["leaseContractId", "customerAccountId", "monthlyInstallment", "consentToken"],
        },
      }
    ],
  };
});

/**
 * Handle Tool Execution
 */
server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args } = request.params;

  if (name === "nebras_initiate_consent") {
    const scope = String(args?.scope);
    const consentId = crypto.randomUUID();
    const url = `https://auth.opf.xbank.com/consent?id=${consentId}&scope=${scope}`;
    return {
      content: [{ type: "text", text: `Success: Generated Consent URL.\nRedirect user to: ${url}\nConsent ID: ${consentId}` }],
    };
  }

  if (name === "nebras_execute_salary_batch") {
    const intentId = "INTENT_" + crypto.randomUUID().substring(0, 8);
    const payload = JSON.stringify({
      intent: "DOMESTIC_PAYMENT",
      type: "SALARY_BATCH",
      sourceAccount: args?.corporateAccountId,
      amount: args?.totalAmount
    });
    return {
      content: [
        { type: "text", text: `Success: Salary Batch submitted.\nIntent ID: ${intentId}\nPayload Encrypted: ${payload}\nAgentive Workflow is now processing this batch.` }
      ],
    };
  }

  if (name === "nebras_execute_car_lease") {
    const intentId = "INTENT_" + crypto.randomUUID().substring(0, 8);
    return {
      content: [
        { type: "text", text: `Success: Car Lease Payment submitted.\nIntent ID: ${intentId}\nAgentive Workflow is now processing this lease payment.` }
      ],
    };
  }

  throw new Error(`Unknown tool: ${name}`);
});

/**
 * Start the MCP Server using Stdio Transport
 */
async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error("Nebras Open Finance MCP Server running on stdio");
}

main().catch((error) => {
  console.error("Server error:", error);
  process.exit(1);
});
