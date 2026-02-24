# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**

The codebase currently exhibits a mix of two database access patterns provided by Quarkus Panache:
1. **Active Record Pattern** (`Store.java extends PanacheEntity`): where the entity itself carries static methods for database access (e.g., `Store.findById()`).
2. **Repository Pattern** (`WarehouseRepository implements PanacheRepository`): where a separate class handles data access logic.

**Refactoring Recommendation:**
If I were to maintain this codebase, I would **refactor the `Store` entity to use the Repository Pattern**, aligning it with the `Warehouse` and `Product` implementations.

**Reasoning:**
1. **Consistency**: Having mixed patterns in the same project increases cognitive load and confusion for developers. Uniformity is key for maintainability.
2. **Separation of Concerns**: The Active Record pattern mixes domain state with persistence logic. The Repository pattern cleanly separates the domain model (Entity) from the data access mechanism.
3. **Testability**: Repositories are generally easier to mock and inject in unit tests compared to static methods on entities, leading to cleaner test code.
4. **Complexity Management**: As the application grows, complex queries are better encapsulated in a repository than cluttering the entity class.

----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**

**Contract-First (OpenAPI Generation - used for `Warehouse`)**
*   **Pros:**
    *   **Single Source of Truth:** The YAML file is the definite contract. Clients and servers allow parallel development.
    *   **Better Design:** Encourages thinking about the API interface and data structures before coding details.
    *   **Generated Client/Server Code:** Reduces boilerplate and type errors.
*   **Cons:**
    *   **Tooling Overhead:** Requires build plugins and generation steps.
    *   **Rigidity:** Changing the API requires editing the YAML and regenerating, which can be slower for rapid prototyping.

**Code-First (JAX-RS Annotations - used for `Product` & `Store`)**
*   **Pros:**
    *   **Development Speed:** Faster to implement and iterate, especially for simple CRUD implementations.
    *   **Simplicity:** No extra build steps or external YAML files to manage manually.
*   **Cons:**
    *   **Documentation Drift:** The generated OpenAPI spec might lag behind or strictly reflect code implementation details rather than intended design.
    *   **Tighter Coupling:** The API contract is tightly coupled to the implementation language/framework.

**My Choice:**
For a monolithic enterprise application or a microservices environment with multiple teams, I would choose **Contract-First (OpenAPI)**. It forces API design discussions to happen early, prevents accidental breaking changes, and allows frontend/mobile teams to generate their clients independently. The strict contract is worth the slight tooling overhead.

----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**

**Prioritization Strategy:**
1.  **Integration Tests (High Priority):** Given this is a backend service relying heavily on database integrity and API contracts, integration tests (using `@QuarkusTest`) provide the highest ROI. They verify the entire stack (Controller -> Service -> Repository -> Database) works together. Tests like `WarehouseEndpointIT` are crucial.
2.  **Unit Tests (Medium Priority):** I would focus unit tests strictly on **business logic**, such as the `UseCases` (e.g., `ReplaceWarehouseUseCase` logic) and complex services (`FulfillmentService`). Testing simple getters/setters or standard framework wiring via unit tests is often redundant.
3.  **End-to-End/Contract Tests:** If interacting with external systems, I would add contract tests (e.g., Pact) later.

**Focus Types:**
*   **Validation Logic:** Ensure all business rules (max capacity, stock limits) are tested with edge cases.
*   **Error Handling:** Verify that the system explicitly handles failure modes (DB down, Invalid Input) gracefully.

**Ensuring Effective Coverage:**
1.  **Automated Quality Gates:** Integrate **Jacoco** into the CI/CD pipeline. Set a hard failure threshold (e.g., 80%) to prevent merging code that lowers coverage.
2.  **Code Reviews:** Mandate that every PR which introduces business logic must include corresponding tests.
3.  **Mutation Testing:** Periodically use tools like PITest to run mutation testing. This modifies code to ensure tests fail when logic changes, verifying that tests are strictly asserting behavior and not just executing lines ("assertion free" testing).