\# Concurrent Seat Booking System



A Spring Boot backend that demonstrates safe handling of concurrent seat bookings — the core challenge in any ticketing/reservation system: \*\*preventing two users from booking the same seat at the same time.\*\*



\## Why This Project



Most CRUD-style resume projects don't demonstrate anything about correctness under concurrent load. This one does. It uses \*\*pessimistic database locking\*\* to guarantee that when two requests race to book the same seat, exactly one succeeds and the other is cleanly rejected — proven with real concurrent requests, not just claimed in a design doc.



\## Tech Stack



\- \*\*Java 21\*\* / \*\*Spring Boot\*\*

\- \*\*Spring Data JPA\*\* (Hibernate) — persistence layer

\- \*\*PostgreSQL\*\* — relational database, chosen specifically for its row-level locking support (`SELECT ... FOR UPDATE`)

\- \*\*Spring Security + JWT\*\* (`jjwt`) — stateless authentication and role-based authorization

\- \*\*Maven\*\* — build tool



\## Core Concepts Demonstrated



\### 1. Pessimistic Locking to Prevent Double-Booking



The seat-locking endpoint uses a custom repository query with `@Lock(LockModeType.PESSIMISTIC\_WRITE)`, which Hibernate translates to `SELECT ... FOR UPDATE`:



```java

@Lock(LockModeType.PESSIMISTIC\_WRITE)

@Query("SELECT s FROM Seat s WHERE s.id = :id")

Optional<Seat> findByIdForUpdate(@Param("id") Long id);

```



When two requests hit `POST /seats/{id}/lock` for the same seat at nearly the same instant:

\- The first request's transaction acquires an exclusive row lock on that seat.

\- The second request's `SELECT` \*\*blocks\*\* until the first transaction commits.

\- Once unblocked, the second request sees the seat's updated status (`LOCKED`) and is correctly rejected with a `409 Conflict` — rather than both requests succeeding and double-booking the seat.



This was verified by firing two near-simultaneous requests at the live endpoint: one returned `200 OK` with the seat locked, the other returned a clean `409 Conflict`.



\### 2. Optimistic Locking (secondary mechanism)



The `Seat` entity also carries a `@Version` column, enabling optimistic locking as an alternative concurrency strategy — evaluated and understood, though pessimistic locking was chosen for the actual seat-lock operation since it's a high-contention moment where blocking (rather than failing and retrying) is the more appropriate tradeoff.



\### 3. Idempotency Keys



`POST /bookings` accepts a client-generated `idempotencyKey`. If a request is retried with the same key (e.g. due to a network timeout or duplicate click), the server returns the \*original\* booking instead of creating a duplicate or double-locking a seat — the same pattern used by payment APIs like Stripe to prevent duplicate charges on retry.



\### 4. Explicit State Machines



Rather than boolean flags, both `Seat` and `Booking` move through explicit enum-based states:



```

SeatStatus:     AVAILABLE → LOCKED → BOOKED

&#x20;                             ↓

&#x20;                         AVAILABLE (on cancellation/expiry)



BookingStatus:  PENDING → CONFIRMED

&#x20;                   ↓

&#x20;               CANCELLED / EXPIRED

```



\### 5. Automatic Seat Expiry



A `@Scheduled` background job runs every 60 seconds, finds `PENDING` bookings whose lock has expired (5-minute window), and automatically releases the seat back to `AVAILABLE` — preventing seats from being held indefinitely by abandoned bookings.



\### 6. Mock Payment + Booking Lifecycle



`POST /bookings/{id}/pay` simulates a payment gateway (70% success rate). On success, the booking is `CONFIRMED`. On failure, the booking is `CANCELLED` \*\*and the seat is released back to `AVAILABLE`\*\* — so a failed payment doesn't permanently lock out a seat.



\### 7. JWT Authentication + Role-Based Access Control



\- `POST /auth/signup` / `POST /auth/login` issue JWTs containing the user's email and role.

\- A custom `JwtAuthFilter` validates the token on every request and populates Spring Security's context.

\- Admin-only endpoints (e.g. `GET /bookings`) are restricted via `hasRole("ADMIN")` — verified with both a passing `ADMIN` token and a rejected `USER` token (`403 Forbidden`).



\## Architecture



```

Controller Layer   → REST endpoints, request/response mapping (DTOs)

Service Layer      → Business logic, transaction boundaries (@Transactional)

Repository Layer   → Spring Data JPA interfaces, custom locking queries

Entity Layer       → JPA entities with explicit state-machine enums

Security Layer     → JWT generation/validation, Spring Security filter chain

Exception Layer    → Custom exceptions + @RestControllerAdvice for clean error responses

```



\*\*Key design choice:\*\* `BookingService` depends on `SeatService` (not `SeatRepository` directly) to reuse the pessimistic-locking logic rather than duplicating it — the seat-locking code path exists in exactly one place.



\## API Endpoints



| Method | Endpoint | Auth | Description |

|---|---|---|---|

| POST | `/auth/signup` | Public | Create a new user |

| POST | `/auth/login` | Public | Authenticate, receive JWT |

| POST | `/shows` | Authenticated | Create a show |

| POST | `/seats/{id}/lock` | Authenticated | Lock a seat (pessimistic lock) |

| POST | `/bookings` | Authenticated | Create a booking (idempotent) |

| POST | `/bookings/{id}/pay` | Authenticated | Simulate payment for a booking |

| GET | `/bookings` | \*\*ADMIN only\*\* | View all bookings |



\## Error Handling



A global `@RestControllerAdvice` converts internal exceptions into structured JSON responses with correct HTTP status codes:



```json

{

&#x20; "timestamp": "2026-09-04T13:08:28.395337",

&#x20; "status": 409,

&#x20; "error": "Seat is not available"

}

```



| Exception | HTTP Status |

|---|---|

| `ResourceNotFoundException` | 404 Not Found |

| `SeatUnavailableException` | 409 Conflict |



\## Running Locally



1\. Create a PostgreSQL database: `booking\_system`

2\. Configure `src/main/resources/application.properties` with your DB credentials

3\. Run:

&#x20;  ```

&#x20;  mvnw.cmd spring-boot:run

&#x20;  ```

4\. Tables are auto-created on first run (`spring.jpa.hibernate.ddl-auto=update`)



\## What I'd Add With More Time



\- Redis-backed distributed locking, to extend the concurrency guarantee across multiple application instances (the current pessimistic lock only works correctly with a single DB serving as the source of truth — which is true here, but wouldn't scale to a multi-region deployment without a distributed lock)

\- BCrypt password hashing (currently plaintext comparison — a known, intentional simplification for this stage of the project)

\- A saga/compensation pattern for payment failures in a more complex multi-step checkout flow

\- Formal concurrent JUnit test suite (currently verified manually via concurrent Postman/curl requests)

