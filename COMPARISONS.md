# Framework Comparisons: Why Zerocode?

Choosing a testing framework is rarely just a technical decision—it's a workflow decision. It dictates how fast your team can ship, how painful it is to maintain regression suites, and whether testing is something only dedicated SDETs can touch or an activity the entire engineering team shares.

This document breaks down **what makes Zerocode different**, how it compares to established tools like **REST-Assured**, **Karate**, **Postman**, and **Cucumber**, and why production engineering teams chose it to validate their microservices, Kafka data streams, and database pipelines.

---

## The Core Frustrations with Traditional Testing

Most engineering teams run into a predictable set of headaches as their systems grow into distributed microservices:

1. **The Glue-Code Tax:** Frameworks like Cucumber require maintaining two separate realities: feature files written in English, and hundreds of lines of Java "glue code" backing every step. When an API changes, you fix the feature, the step definitions, the assertions, and the model POJOs.
2. **The Multi-Protocol Nightmare:** A modern user journey doesn't live in HTTP alone. A single checkout flow might receive an HTTP POST, publish a message to Apache Kafka, process an event asynchronously, and persist a record into PostgreSQL. Stitching this together traditionally means gluing together REST-Assured, KafkaProducer/Consumer clients, JDBC helpers, and `Thread.sleep` hacks.
3. **The Dev-to-QA Divide:** Complex programmatic frameworks require deep Java skills, excluding manual QAs and business testers. Conversely, GUI-based tools often result in massive JSON exports that cause nightmares in Git merges and CI pipelines.
4. **Duplicate Effort for Load Testing:** After spending months writing functional regression suites, teams often have to start from scratch with JMeter or Gatling just to test system behavior under load.

**Zerocode was built specifically to eliminate these four pain points.**

---

## What Makes Zerocode the Simplest Tool?

Simplicity in Zerocode isn't about having fewer features; it's about **removing unnecessary layers between your intention and your execution**.

### 1. Pure Declarative JSON/YAML (No Step Definitions)
In Zerocode, tests are plain JSON or YAML documents. There is **zero glue code**. The scenario directly describes the endpoint, the payload sent, and the expected structure. If you know what your API's payload looks like, you already know how to write a Zerocode test.

### 2. Payload-First, Lenient Assertions
In traditional frameworks, asserting a 20-field JSON response requires 20 separate assertion lines:
```java
// Traditional imperative approach: repetitive and brittle
response.then()
    .statusCode(200)
    .body("id", equalTo(123))
    .body("name", equalTo("Alice"))
    .body("address.city", equalTo("London"))
    // ... 15 more lines
```
With Zerocode's default `LENIENT` mode, you simply paste the JSON fields you care about into the `assertions` block:
```json
"assertions": {
  "status": 200,
  "body": {
    "id": 123,
    "name": "Alice",
    "address": { "city": "London" }
  }
}
```
> **Note:** Zerocode also accepts `"verify"` as an alias for `"assertions"`. Both work identically, but `"assertions"` is the preferred convention.
Zerocode matches the structure and values you specified, while automatically ignoring dynamic fields (like timestamps, generated UUIDs, or extra metadata) unless you opt for `STRICT` mode.

### 3. Native Multi-Protocol Workflows in One File
You can chain actions across different transport layers in the same test file without writing any driver code:
* **Step 1:** Call a REST endpoint to trigger an action.
* **Step 2:** Assert that an Apache Kafka topic received the event.
* **Step 3:** Query a relational database (Postgres/MySQL/Oracle) to verify state.

### 4. Reusing Functional Scenarios for Load & Performance
You do not need a separate tool for load testing. By adding the `@LoadWith` annotation and using `ZeroCodeLoadRunner` (JUnit 4) or the JUnit 5 parallel load extension on your existing functional test suite, Zerocode executes the exact same JSON scenarios with hundreds or thousands of virtual users.

### 5. IDE Autocompletion via Published JSON Schema
Zerocode publishes a [JSON Schema](schema/zerocode-scenario-schema.json) (Draft-07) for scenario files. Point your IDE at it and get **real-time autocompletion, inline validation, and error highlighting** as you write test scenarios — a capability none of the compared frameworks offer for their test definition files.

---

## Comparison Matrix

| Capability / Feature | **Zerocode** | **REST-Assured** | **Karate** | **Postman + Newman** | **Cucumber (BDD)** |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Test Definition** | Declarative JSON / YAML | Programmatic Java DSL | Gherkin + JavaScript | GUI / Large JSON | Gherkin (`.feature`) |
| **Glue Code Required** | ❌ **None** | N/A (Code only) | ❌ **None** | ❌ **None** | ⚠️ **Heavy** (Step Defs) |
| **Coding Skill Needed** | **Minimal** (JSON / YAML) | **High** (Java) | **Low–Medium** | **Low** (JS for scripts)| **High** (Java/Python) |
| **Apache Kafka Testing** | ✅ **Native & built-in** | ❌ Needs custom code | ⚠️ Limited / plugins | ❌ Third-party plugins | ❌ Needs custom code |
| **Database Verification**| ✅ **Native SQL/JDBC** | ❌ Needs custom code | ⚠️ Java interop | ❌ Via scripts / APIs | ❌ Needs custom code |
| **Data Piping Between Steps** | ✅ Declarative `${STEP.response...}` | ⚠️ Manual variables in code | ✅ Native JS variables | ⚠️ `pm.environment.set` | ⚠️ Shared world context |
| **Handling Async & Retries** | ✅ Declarative `"retry"` block | ⚠️ External (Awaitility) | ⚠️ Retry keywords | ⚠️ Scripted loops | ⚠️ Custom code |
| **Performance Testing** | ✅ Reuses functional tests | ❌ Separate tool needed | ✅ Built-in Gatling hooks | ⚠️ Postman Cloud / CLI | ❌ Separate tool needed |
| **Git & PR Review Experience** | ✅ Modular, clean JSON files | ✅ Standard Java diffs | ✅ Readable feature diffs | ⚠️ Large monolithic JSON | ✅ Readable feature diffs |
| **CI / CD Integration** | ✅ Native JUnit runner | ✅ Native JUnit runner | ✅ JUnit / CLI | ⚠️ Node.js / Newman | ✅ Native JUnit runner |

---

## Deep Dive: Zerocode vs. Other Tools

### 1. Zerocode vs. REST-Assured

REST-Assured is the gold standard for Java developers writing unit-level API tests. However, it requires strong Java proficiency and quickly accumulates boilerplate.

* **Where REST-Assured Shines:** Great if you are writing unit-level API tests inside a Spring Boot project, need fine-grained mock integration with Mockito, or want full programmatic control over HTTP clients.
* **Where Zerocode Wins:**
  * **No compilation overhead:** Scenarios are declarative data files, making them faster to author, review, and adjust.
  * **Accessible to non-programmers:** QA analysts and testers don't need Java fluency to write complete integration tests.
  * **Multi-step chaining:** Chaining 5 API calls in REST-Assured involves extracting variables, creating DTOs, and passing states. In Zerocode, referencing `${step_1.response.body.token}` in step 2 is declarative and automatic.
  * **Out-of-the-box Kafka & DB:** REST-Assured only knows HTTP. Zerocode speaks HTTP, Kafka, and SQL natively.

---

### 2. Zerocode vs. Karate Framework

Karate is a powerful framework that blends Gherkin syntax with JavaScript engine logic.

* **Where Karate Shines:** Teams that like BDD-style syntax without having to write Cucumber step definitions, or those who want a unified tool for browser UI automation and APIs.
* **Where Zerocode Wins:**
  * **Pure JSON vs. Hybrid Scripting:** While Karate simplifies Gherkin, complex tests often become a mix of Gherkin syntax with embedded JavaScript snippets. Zerocode keeps test declarations purely structural in standard JSON/YAML.
  * **First-Class Kafka Support:** Zerocode was designed from the ground up to handle Kafka producers, consumers, and offset commits with deep assertion semantics.
  * **Simpler mental model:** If your team works primarily with JSON APIs, testing JSON using JSON is intuitive and eliminates syntax switching.

---

### 3. Zerocode vs. Postman / Newman

Postman is loved for its visual interface and quick exploratory testing.

* **Where Postman Shines:** Rapid ad-hoc debugging, exploratory manual API calls, and sharing API specifications via workspaces.
* **Where Zerocode Wins:**
  * **Version control & merge conflicts:** Postman collections are exported as massive, single-file JSON blobs that frequently result in nasty Git merge conflicts when multiple engineers work on tests. Zerocode tests are small, modular files organized logically in standard source folders.
  * **Local IDE integration:** Zerocode tests run directly inside IntelliJ IDEA, Eclipse, or VS Code with standard test runners (JUnit 4 and 5), allowing developers to run a single scenario with a right-click.
  * **Enterprise CI/CD Pipelines:** Zerocode runs natively inside the standard Java build pipeline (`mvn test` or `gradle test`) without needing Node.js or Newman installations in CI runners.

---

### 4. Zerocode vs. Cucumber / SpecFlow

Cucumber popularized Behavior-Driven Development (BDD), but teams often struggle with its maintenance burden.

* **Where Cucumber Shines:** Fostering cross-functional communication on business rules where business stakeholders actively read Gherkin specifications.
* **Where Zerocode Wins:**
  * **No Step-Definition Glue Code:** For technical API and integration testing, writing regex-matched step definitions for every sentence is high-friction overhead. Zerocode provides the readable, self-documenting benefits of BDD scenarios without the maintenance tax of backing code.
  * **Less Indirection:** What you see in the JSON file is exactly what gets executed. You never have to command-click through three layers of Java step definitions to figure out what a test step is actually doing.

---

## A Complete Example: What a Zerocode Test Actually Looks Like

Rather than abstract descriptions, here is a self-contained, runnable Zerocode scenario that validates a REST API. This is the entire test — no Java code, no step definitions, no glue:

```json
{
  "scenarioName": "Validate customer API - create and retrieve",
  "steps": [
    {
      "name": "create_customer",
      "url": "/api/v1/customers",
      "method": "POST",
      "request": {
        "headers": { "Content-Type": "application/json" },
        "body": {
          "name": "Alice",
          "email": "alice@example.com"
        }
      },
      "assertions": {
        "status": 201,
        "body": {
          "name": "Alice"
        }
      }
    },
    {
      "name": "fetch_created_customer",
      "url": "/api/v1/customers/${create_customer.response.body.id}",
      "method": "GET",
      "request": {},
      "retry": {
        "max": 3,
        "delay": 500
      },
      "assertions": {
        "status": 200,
        "body": {
          "name": "Alice",
          "email": "alice@example.com"
        }
      },
      "verifyMode": "LENIENT"
    }
  ]
}
```

Notice:
- **Step 2 automatically references Step 1's response** via `${create_customer.response.body.id}` — no manual variable extraction.
- **Retry logic is declarative** — if the newly created customer isn't immediately available, Zerocode retries up to 3 times with a 500ms delay.
- **`LENIENT` mode** means the test passes even if the actual response contains extra fields (like `createdAt` or `updatedAt`).
- The corresponding Java test class is minimal — just an empty method with an annotation pointing to this JSON file.

---

## How Zerocode Solves Real-World Testing Challenges

### Challenge 1: Asynchronous Event Processing & Eventual Consistency
**The Problem:** Microservices and Kafka pipelines don't respond immediately. Writing tests for eventual consistency usually leads to flaky tests full of arbitrary sleeps (`Thread.sleep(5000)`).  
**The Zerocode Solution:** Built-in polling and retry configurations right in the test step:
```json
"retry": {
  "max": 5,
  "delay": 1000
}
```
Zerocode will repeatedly execute or poll the step until the assertions pass or the retry budget is exhausted, eliminating test flakiness cleanly.

### Challenge 2: Cross-Service State Transfer
**The Problem:** Authenticating in Step 1, creating a customer in Step 2, and retrieving the customer's orders in Step 3 requires capturing IDs and auth headers.  
**The Zerocode Solution:** Dynamic placeholder resolution:
```json
"headers": {
  "Authorization": "Bearer ${login_step.response.body.authToken}"
},
"url": "/api/v1/customers/${create_customer_step.response.body.id}/orders"
```
No variables to declare, no context objects to manage.

---

## Summary: When Should You Choose Zerocode?

**Zerocode is the right choice if:**
- You want to write automated integration tests **without writing boilerplate code**.
- Your architecture includes **REST APIs, Apache Kafka event streams, and Databases** that need to be verified together.
- You have a team of **developers and QA engineers** who need a common, readable test format.
- You want to run **load and performance tests** without rebuilding your test suite in a separate tool.
- You want seamless integration with standard **Java build tools (Maven, Gradle)** and **IDEs (IntelliJ, Eclipse)**.

---

*Zerocode is trusted in mission-critical environments by organizations including **Vocalink (Mastercard)**, **HSBC**, and the **UK Home Office** to deliver reliable, production-ready microservices and data pipelines.*

---

## Learn More

- **Documentation:** [zerocode-tdd.tddfy.com](https://zerocode-tdd.tddfy.com) — Indexed, searchable reference
- **Developer Guide:** [GitHub Wiki](https://github.com/authorjapps/zerocode/wiki#developer-guide)
- **JSON Schema for Scenarios:** [`schema/zerocode-scenario-schema.json`](schema/zerocode-scenario-schema.json)
- **Quick Start (CLI):** [Steply](https://github.com/QABEES/steply?tab=readme-ov-file#steply)
- **Community:** [Slack](https://join.slack.com/t/zerocode-workspace/shared_invite/enQtNzYxMDAwNTQ3MjY1LTA2YmJjODJhNzQ4ZjBiYTQwZDBmZmNkNmExYjA3ZDk2OGFiZWFmNWJlNGRkOTdiMDQ4ZmQyNzcyNzVjNWQ4ODQ) · [Mailing List](https://groups.google.com/forum/#!forum/zerocode-automation)
