# Insurance Management System — Sawai Associates Technical Assignment

Java 17 + Spring Boot + Spring Data JPA + MySQL + Vanilla JS frontend.

## 1. Project Structure

```
insurance-management-system/
├── pom.xml
├── database/
│   └── schema.sql                 # MySQL schema + seed data
├── src/main/java/com/sawai/insurance/
│   ├── InsuranceManagementSystemApplication.java
│   ├── config/
│   │   ├── AuthInterceptor.java   # custom X-Auth-Token middleware
│   │   └── WebConfig.java         # registers interceptor + CORS
│   ├── entity/
│   │   ├── Customer.java
│   │   ├── Policy.java
│   │   └── Lead.java
│   ├── repository/
│   │   ├── CustomerRepository.java
│   │   ├── PolicyRepository.java
│   │   └── LeadRepository.java
│   ├── controller/
│   │   ├── CustomerController.java
│   │   ├── PolicyController.java
│   │   └── LeadController.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── ResourceNotFoundException.java
└── src/main/resources/
    ├── application.properties
    └── static/
        ├── index.html
        ├── style.css
        └── script.js
```

## 2. Database Setup

1. Start MySQL locally.
2. Run the schema file (creates the database, tables, and seed data):
   ```
   mysql -u root -p < database/schema.sql
   ```
3. Update `src/main/resources/application.properties` with your MySQL username/password if different from `root/root`.

   > Note: `spring.jpa.hibernate.ddl-auto=update` is also set, so Hibernate will create/verify tables automatically from the entity classes even if you skip step 2 — but the `.sql` file is provided as required by the assignment.

## 3. Run the Application

```
mvn spring-boot:run
```

App runs at `http://localhost:8080`. Open that URL in a browser to use the frontend dashboard directly (it's served from `src/main/resources/static`).

## 4. Authentication

Every `/api/**` route is guarded by a custom `HandlerInterceptor` (`AuthInterceptor.java`) that reads a required header:

```
X-Auth-Token: <token>
```

Two valid tokens are configured (see `AuthInterceptor.TOKEN_ROLE_REGISTRY`):

| Token | Role |
|---|---|
| `admin-9f3a7c2e-token` | ADMIN |
| `agent-4b1d8e6f-token` | AGENT |

**Rules enforced:**
- Missing/invalid token → `401 Unauthorized`
- ADMIN and AGENT can both `GET`, `POST`, `PUT`
- Only ADMIN can `DELETE` — an AGENT attempting delete gets `403 Forbidden`

The frontend has a "Sign in as ADMIN/AGENT" dropdown at the top that sends the matching token automatically, so you can demo both roles live.

## 5. API Reference

Base URL: `http://localhost:8080/api`

**Customers**
- `GET /customers` — list all
- `GET /customers/{id}` — get one
- `GET /customers/search?name=xyz` — search by first/last name
- `POST /customers` — create
- `PUT /customers/{id}` — update
- `DELETE /customers/{id}` — ADMIN only

**Policies**
- `GET /policies` — list all
- `GET /policies/{id}` — get one
- `GET /policies/customer/{customerId}` — all policies for a customer
- `POST /policies` — create (body includes `customerId`)
- `PUT /policies/{id}` — update
- `DELETE /policies/{id}` — ADMIN only

**Leads**
- `GET /leads` — list all
- `GET /leads/{id}` — get one
- `GET /leads/status/{status}` — filter by status
- `POST /leads` — create
- `PUT /leads/{id}` — update
- `DELETE /leads/{id}` — ADMIN only

### Example (curl)

```bash
curl -H "X-Auth-Token: admin-9f3a7c2e-token" http://localhost:8080/api/customers

curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -H "X-Auth-Token: admin-9f3a7c2e-token" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","phoneNumber":"9999999999","dateOfBirth":"1999-01-01","accountStatus":"ACTIVE"}'

curl -X DELETE http://localhost:8080/api/customers/1 \
  -H "X-Auth-Token: agent-4b1d8e6f-token"
# -> 403 Forbidden (AGENT cannot delete)
```

Import these into Postman, or hit `http://localhost:8080/api/customers` directly for a Swagger-style walkthrough during the interview.

## 6. Design Notes (for the interview walkthrough)

- **Layered architecture:** Controller → Repository (Spring Data JPA) → MySQL. No service layer bloat was added since the CRUD logic is thin; this keeps the code honest about what it's doing rather than adding an unnecessary abstraction layer.
- **Interceptor over Spring Security:** Chose `HandlerInterceptor` instead of full Spring Security because the assignment explicitly asked for a "custom middleware interceptor layer" with a simple header token — Spring Security would be overkill and would obscure the exact mechanism being tested.
- **DTO for Policy writes:** `PolicyController.PolicyRequest` record decouples the wire format (plain `customerId`) from the JPA entity graph (`Customer` object), avoiding over-fetching and n+1 issues on writes.
- **Global exception handler:** Centralizes 404 (not found), 409 (duplicate email/policy number), and 400 (validation) responses so every controller stays focused on business logic.
- **No mock data / no simulated delays:** Every screen in the frontend is backed by a real fetch call to the database-backed API, per the assignment's explicit constraint.

## 7. Pushing to GitHub

```bash
cd insurance-management-system
git init
git add .
git commit -m "Insurance Management System - Sawai Associates assignment"
git branch -M main
git remote add origin <your-empty-public-repo-url>
git push -u origin main
```

Make sure the repository is set to **Public** before submitting the link.
