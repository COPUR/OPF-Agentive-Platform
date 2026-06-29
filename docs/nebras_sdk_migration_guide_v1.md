# Nebras SDK Migration Guide (v1)

This guide helps enterprise Third Party Providers (TPPs)—such as Payroll or Car Leasing companies—migrate off our legacy SOA BaaS endpoints and onto the UAECB Open Finance **Nebras** specification.

## Why Migrate?
The legacy SOA endpoints required rigid XML payloads and exposed our internal Mainframe structures. The new Agentive Nebras platform requires JSON payloads, DPoP (Demonstrating Proof-of-Possession) cryptography, and FAPI-compliant security. 

To prevent you from having to rewrite your entire cryptography and mapping layers, we have built the **`opf-nebras-java-sdk`**.

## 1. Installation
Add the SDK to your Maven `pom.xml`:
```xml
<dependency>
    <groupId>com.xbank.opf.sdk</groupId>
    <artifactId>opf-nebras-java-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 2. Architecture & Diagrams
To help you visualize how the SDK encapsulates the Open Finance complexity, please review the following diagrams:

### SDK Class Architecture
![SDK Class Diagram](diagrams/nebras_sdk_class_v1.svg)

### SDK Runtime Flow (Sequence)
![SDK Sequence Diagram](diagrams/nebras_sdk_sequence_v1.svg)

## 3. The New Implementation Flow

### Step A: Consent Management & Tokenization (SCA)
Unlike the legacy BaaS system, Open Finance requires explicit Strong Customer Authentication (SCA).

```java
import com.xbank.opf.sdk.security.ConsentManager;
import com.xbank.opf.sdk.security.ApiTokenizationService;

ConsentManager consentMgr = new ConsentManager();
// 1. Generate redirect URL for the user to authenticate on UAE Pass
String authUrl = consentMgr.initiateConsentFlow("DOMESTIC_PAYMENT");
System.out.println("Redirect user to: " + authUrl);

// 2. Poll or wait for the user to finish on their mobile device
boolean isAuthorized = consentMgr.checkConsentStatus("consent_123");

// 3. Exchange the authorized consent for a secure API token
ApiTokenizationService tokenizer = new ApiTokenizationService();
String apiToken = tokenizer.exchangeConsentForToken("consent_123");
```

### Step B: Executing the Intent
Once you have the `apiToken`, the SDK automatically maps your domain request to JSON and attaches the cryptographic DPoP signatures.

```java
import com.xbank.opf.sdk.NebrasClient;
import com.xbank.opf.sdk.domain.SalaryBatchRequest;
import com.xbank.opf.sdk.WorkflowWatcher;

// Initialize with your OAuth credentials
NebrasClient client = new NebrasClient("your_client_id", "your_private_key_pem");
SalaryBatchRequest request = new SalaryBatchRequest("CORP_12345", 50, 250000.00);

// Execute! SDK handles JSON mapping, Authorization header, and DPoP
String intentId = client.executeSalaryBatch(request);

// Await Cognitive completion
WorkflowWatcher watcher = new WorkflowWatcher();
String status = watcher.waitForCompletion(intentId);
```

## 4. Supported Domain Models
- `SalaryBatchRequest`: For automated monthly payrolls.
- `CarLeaseLoanRequest`: For recurring auto-loan payments.

## 5. Model Context Protocol (MCP) Integration
Because the Nebras SDK abstracts away complex cryptographic signatures and consent tokenization, it is explicitly designed to be **Agent-Friendly**. 

If your company employs AI agents, you can trivially wrap this SDK inside a **Model Context Protocol (MCP)** Server. By exposing `executeSalaryBatch()` and `initiateConsentFlow()` as MCP Tools, your internal LLM agents can autonomously reason about, approve, and securely execute payroll and loan operations against the Bank's API Souq, without writing custom DPoP or OAuth2 integration code!
