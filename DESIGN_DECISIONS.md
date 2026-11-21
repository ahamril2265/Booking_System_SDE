# Design Decisions — Concurrent Seat Booking System

The core problem: two users try to book the same seat at the same moment, and exactly one must succeed. Every decision below serves that guarantee.

## Stack

Java 21 · Spring Boot 4.1 · Spring Data JPA · PostgreSQL · Spring Security + JWT (jjwt)

## Decisions

| Decision | Chosen over | Why | Trade-off |
| --- | --- | --- | --- |
| PostgreSQL | MongoDB, MySQL | Seats, shows and bookings are relational, and booking needs ACID transactions with row-level locks (`SELECT ... FOR UPDATE`). | MySQL InnoDB would also work; a document store makes multi-record locking harder. |
| Pessimistic locking (`PESSIMISTIC_WRITE`) | Optimistic locking (`@Version`) | A popular seat is a high-contention moment. Making the second request wait is better than letting it fail and retry. | Requests queue under heavy contention. Optimistic locking fits when conflicts are rare. |
| Database row lock | Redis distributed lock | The lock lives with the data and works across any number of application instances sharing the database. | A single database is the coordination point. |
| Client-supplied idempotency key (unique column) | Deduplicating on user + seat | Network retries and double clicks return the original booking instead of creating a second one. | Clients must send a key. |
| Explicit state machines (`AVAILABLE → LOCKED → BOOKED`; `PENDING → CONFIRMED / EXPIRED / CANCELLED`) | Boolean flags | Every allowed transition is explicit, so invalid states are rejected. | More code than a flag. |
| `@Scheduled` expiry job (every 60 s) releases unpaid holds after 5 minutes | Redis key TTL, database triggers | Simple, and it runs inside the same transaction model as everything else. | A seat may be released up to 60 s late. Row locks keep it safe if several instances run the job. |
| Payment locks the booking row and accepts only `PENDING`, unexpired bookings | Checking status without a lock | Prevents an expired booking being paid after its seat was released, which would double-book the seat. | One extra locked read per payment. |
| Stateless JWT authentication | Server-side sessions | Any instance can verify a request without shared session storage. | Tokens can't be revoked before expiry without extra work. |
| Seat locking only through `SeatService` | Services calling `SeatRepository` directly | Locking logic lives in exactly one place. | One more layer. |

## Verified behaviour

Two simultaneous lock requests for the same seat: one returns `200`, the other `409 Conflict`.

## Next steps

- Take the user ID from the JWT instead of the request body, and check booking ownership on payment.
- Move the seat to `BOOKED` when payment succeeds.
- Replace the simulated payment with a real gateway and a webhook callback.
- Return the original booking (not `409`) when two requests with the same idempotency key arrive at the same instant.
