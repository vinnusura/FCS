# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
**Answer:**
**Challenges:**
1. **Shared Resources:** Allocating costs for shared resources (e.g., a truck delivering to multiple stores, or a warehouse storing products for multiple business units) is difficult. We need a fair allocation key (e.g., volume, weight, time).
2. **Dynamic Operations:** Fulfillment is fast-paced. Tracking labor hours per specific task (e.g., "picking for Store A") vs. general "picking" can be administratively burdensome and prone to error without automation.
3. **Indirect Costs:** Overhead (electricity, rent, management salaries) needs to be apportioned. Choosing the right driver (e.g., square footage, headcount) affects profit margins of individual units.
4. **Data Fragmentation:** Costs often sit in different systems (HR for labor, Logistics for transport, ERP for inventory). Unifying this data for granular reporting is a major integration challenge.

**Considerations:**
*   **Granularity vs. Effort:** How precise do we need to be? Is Activity-Based Costing (ABC) worth the implementation effort?
*   **Standardization:** Are all warehouses using the same expense categories?
*   **Tagging:** Can we implement tagging at the source (e.g., time-tracking apps for staff, cost center coding for invoices)?

----

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
**Answer:**
**Strategies:**
1.  **Inventory Optimization:** Reduce carrying costs by optimizing stock levels (JIT, better forecasting) to free up working capital and warehouse space.
2.  **Route Optimization:** Use algorithms to plan delivery routes, saving fuel and time. Consolidate shipments where possible.
3.  **Warehouse Automation:** Implement WMS (Warehouse Management Systems) to optimize picking paths, reduce travel time, and improve labor efficiency (or use robotics for repetitive tasks).
4.  **Vendor Negotiation:** Consolidate procurement to negotiate bulk discounts on packaging and supplies.

**Identification & Implementation:**
*   **Data Analysis:** Analyze historical data to find outliers (e.g., routes with high fuel consumption, SKUs with low turnover).
*   **Pareto Principle:** Focus on the 20% of causes driving 80% of costs (usually labor and transport).
*   **Pilot Testing:** Implement changes in one warehouse or region first to measure impact before successful rollout.

----

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
**Answer:**
**Benefits:**
1.  **Single Source of Truth:** Eliminates discrepancies between "operational numbers" and "finance numbers."
2.  **Faster Closing:** Automates data flow, reducing the time needed for month-end financial closing.
3.  **Real-time Decision Making:** Managers can see the financial impact of operational decisions immediately, not weeks later.

**Ensuring Seamless Integration:**
*   **API-First Approach:** Use RESTful APIs or Event-Driven Architecture (e.g., Kafka) for real-time updates rather than nightly batch files.
*   **Idempotency & Reconciliation:** Build systems that handle retries gracefully (idempotency) and run automated reconciliation jobs to catch data drift.
*   **Master Data Management:** Ensure shared identifiers (Warehouse ID, GL Codes) are consistent across both systems.

----

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
**Answer:**
**Importance:**
*   **Capacity Planning:** Forecasting demand allows us to hire temporary labor or lease extra space *before* the peak season hits.
*   **Cash Flow Management:** Accurate cost forecasts ensure the treasury has liquidity to pay suppliers and payroll.
*   **Performance Management:** Budgets provide a baseline to measure operational efficiency (Actual vs. Planned).

**System Design Considerations:**
*   **Drivers-Based Budgeting:** Build models based on operational drivers (e.g., "cost per order", "units per hour") rather than just flat dollar amounts. This allows the budget to flex with volume changes.
*   **Historical Trends + Seasonality:** The system must ingest historical data and account for seasonality (e.g., holiday spikes).
*   **Scenario Planning:** Allow users to simulate "What-If" scenarios (e.g., "What if fuel prices rise by 10%?").

----

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
**Answer:**
**Preserving History:**
*   **Trend Analysis:** We need historical data to see if the new warehouse is actually performing better (lower cost per unit) than the old one. If we lose history, we lose our baseline.
*   **Audit & Compliance:** Financial records must be kept for tax and audit purposes for several years, regardless of operational status.
*   **Asset Depreciation:** The old warehouse layout or assets might still be on the books; we need to track their retirement correctly.

**Relation to Budget:**
*   **Transition Costs:** Developing a new warehouse involves one-time costs (moving, setup, training). These should be tracked separately from ongoing operational costs to not skew the operating budget.
*   **Efficiency Ramp-up:** A new warehouse might be *less* efficient initially. Historical data helps set realistic "ramp-up" budget targets rather than expecting immediate peak performance.

----

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
