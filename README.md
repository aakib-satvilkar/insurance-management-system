## 6. Design Notes

- **Layered architecture:** Controller → Repository (Spring Data JPA) → MySQL. No service layer was added since the CRUD logic is thin, so this keeps things straightforward rather than adding an unnecessary abstraction layer.
- **Interceptor over Spring Security:** Used a plain `HandlerInterceptor` instead of full Spring Security since the requirement was a custom middleware layer with a simple header token — Spring Security would be overkill and would obscure the exact mechanism.
- **DTO for Policy writes:** `PolicyController.PolicyRequest` record decouples the wire format (plain `customerId`) from the JPA entity graph (`Customer` object), avoiding over-fetching and n+1 issues on writes.
- **Global exception handler:** Centralizes 404 (not found), 409 (duplicate email/policy number), and 400 (validation) responses so every controller stays focused on business logic.
- **No mock data / no simulated delays:** Every screen in the frontend is backed by a real fetch call to the database-backed API.