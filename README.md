# Disaster Relief Coordination Platform

Production-grade platform for coordinating disaster response, volunteer operations, relief distribution, and donations.

---

## Tech Stack

| Layer       | Technology                              |
|-------------|------------------------------------------|
| Backend     | Java 17 + Spring Boot 3.2 (Maven)        |
| Database    | MySQL 8 (InnoDB, transactional)          |
| Frontend    | HTML5 + CSS3 + Vanilla JS (ESM modules)  |
| Auth        | JWT (JJWT 0.12) + Spring Security        |
| Real-time   | WebSocket (STOMP over SockJS)            |
| Payment     | Razorpay (full order→verify→webhook)     |
| Containers  | Docker + Docker Compose                  |

---

## Quick Start

### Option A — Docker Compose (recommended)

```bash
cd disaster-relief-platform

# Set your Razorpay keys
export RAZORPAY_KEY_ID=rzp_test_YOUR_KEY
export RAZORPAY_KEY_SECRET=YOUR_SECRET
export RAZORPAY_WEBHOOK_SECRET=YOUR_WEBHOOK_SECRET

docker-compose up -d
```

Backend: http://localhost:8080/api  
Frontend: open `frontend/index.html` in browser (or use Live Server)

### Option B — Manual Setup

**1. MySQL**
```sql
-- Run schema and seed
mysql -u root -p < backend/src/main/resources/db/schema.sql
mysql -u root -p disaster_relief_db < backend/src/main/resources/db/seed.sql
```

**2. Configure `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/disaster_relief_db?...
spring.datasource.username=root
spring.datasource.password=yourpassword

razorpay.key.id=rzp_test_YOUR_KEY_ID
razorpay.key.secret=YOUR_KEY_SECRET
razorpay.webhook.secret=YOUR_WEBHOOK_SECRET
```

**3. Build and Run**
```bash
cd backend
mvn clean package -DskipTests
java -jar target/disaster-relief-platform-1.0.0.jar
```

**4. Frontend**
Open `frontend/index.html` via VS Code Live Server or any static file server.

---

## Demo Credentials

| Role        | Username      | Password      |
|-------------|---------------|---------------|
| Admin       | `admin`       | `password123` |
| Coordinator | `coordinator1`| `password123` |
| Volunteer   | `volunteer1`  | `password123` |

---

## API Reference

### Authentication
```
POST /api/auth/login        — Login
POST /api/auth/register     — Register
```

### Disasters
```
GET    /api/disasters              — List (filterable by status, severity, paginated)
POST   /api/disasters              — Create (Admin/Coordinator)
PUT    /api/disasters/{id}         — Update
PATCH  /api/disasters/{id}/status  — Update status
GET    /api/disasters/nearby       — Geo-query (?lat=&lng=&radiusKm=)
```

### Payments (Razorpay)
```
POST /api/payments/create-order    — Create Razorpay order
POST /api/payments/verify          — Verify signature & capture
POST /api/payments/webhook         — Razorpay webhook (no auth required)
```

### Volunteers
```
GET    /api/volunteers             — List (filterable by availability)
POST   /api/volunteers/register    — Register as volunteer
PATCH  /api/volunteers/{id}/availability — Update availability
GET    /api/volunteers/nearby      — Find nearby (?lat=&lng=&skill=)
```

### Inventory
```
GET    /api/inventory              — List with category filter
GET    /api/inventory/low-stock    — Low stock alert items
POST   /api/inventory              — Add item
PATCH  /api/inventory/{id}/quantity — Update quantity (ADD/SUBTRACT/SET)
```

### Assignments
```
POST   /api/assignments               — Create (Admin/Coordinator)
GET    /api/assignments/disaster/{id} — By disaster
PATCH  /api/assignments/{id}/status   — Update status
```

### Relief Requests
```
GET    /api/requests           — List sorted by urgency DESC
GET    /api/requests/priority  — Top N pending by urgency
POST   /api/requests           — Create
PATCH  /api/requests/{id}/status — Update status
```

### Notifications
```
GET    /api/notifications              — User notifications
GET    /api/notifications/unread-count
PATCH  /api/notifications/mark-all-read
```

---

## Razorpay Integration Flow

```
1. POST /api/payments/create-order
   → Creates Razorpay order via SDK
   → Persists Payment + Donation as PENDING
   → Returns orderId + keyId to frontend

2. Frontend opens Razorpay Checkout modal

3. On success → POST /api/payments/verify
   → HMAC-SHA256 signature verification
   → Updates Payment status to CAPTURED
   → Updates Donation status to CONFIRMED

4. Razorpay Webhook → POST /api/payments/webhook
   → Verifies webhook HMAC signature
   → Idempotency: checks PaymentEvent table before processing
   → Handles: payment.captured, payment.failed, refund.created
```

---

## Architecture

```
Controller → Service → Repository → Database
    ↓             ↓
   DTO         @Transactional
   Mapper      Pessimistic locking (inventory)
               Optimistic locking (@Version on inventory)
```

### Key Design Decisions

- **Inventory**: `@Lock(PESSIMISTIC_WRITE)` prevents race conditions during stock updates
- **Payments**: Idempotency via `PaymentEvent` unique constraint on `(payment_id, event_type)`
- **Volunteer matching**: Haversine formula in JPQL for geo-distance ordering
- **Priority queue**: Relief requests always sorted by `urgency_level DESC, created_at ASC`
- **Notifications**: `@Async` + STOMP WebSocket for real-time push without blocking
- **Audit logging**: Available via `AuditLog` entity for compliance tracking

---

## Running Tests

```bash
cd backend
mvn test
# Uses H2 in-memory DB via application-test.properties
```

---

## Project Structure

```
disaster-relief-platform/
├── backend/
│   ├── src/main/java/com/disasterrelief/
│   │   ├── config/          SecurityConfig, WebSocketConfig, RazorpayConfig
│   │   ├── controller/      REST controllers
│   │   ├── dto/             Request/Response DTOs
│   │   ├── entity/          JPA entities
│   │   ├── exception/       GlobalExceptionHandler + custom exceptions
│   │   ├── repository/      Spring Data JPA repositories
│   │   ├── security/        JWT provider + filter + UserDetailsService
│   │   └── service/         Business logic implementations
│   └── src/main/resources/
│       ├── db/schema.sql    Full MySQL schema
│       ├── db/seed.sql      Demo seed data
│       └── application.properties
├── frontend/
│   ├── index.html           Login page
│   ├── register.html        Registration
│   ├── admin-dashboard.html Stats + overview
│   ├── disasters.html       Disaster management
│   ├── relief-requests.html Priority-sorted requests
│   ├── assignments.html     Volunteer assignment
│   ├── inventory.html       Stock management
│   ├── volunteers.html      Volunteer directory + geo-search
│   ├── donations.html       Razorpay donation page
│   ├── css/main.css         Complete design system
│   └── js/
│       ├── api.js           Full API client
│       ├── auth.js          JWT auth helpers
│       ├── payment.js       Razorpay checkout flow
│       ├── websocket.js     STOMP real-time client
│       └── utils.js         Shared UI utilities
└── docker-compose.yml
```
