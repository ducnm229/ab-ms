# Postman guide: Customer Feign resilience (Resilience4j retry + circuit breaker)

This verifies **retry** (`customer-service-retry`) and **circuit breaker** (`customer-service`) wrapping `CustomerValidationService.validateCustomerOrThrow`, which runs inside `POST /api/orders`.

**Services**

- Order: `http://localhost:9070` (see `server.port`).
- Customer: URL from `clients.customer-service.url` (default `http://localhost:9060`).

**Sample body** (catalog product ids **K100**–**K500**):

```json
{
  "customerId": 1,
  "items": [{ "productId": "K100", "quantity": 1 }]
}
```

**Actuators** (`management.endpoints.web.exposure.include` includes `"*"` in default config):

- `GET http://localhost:9070/actuator/circuitbreakers` — look for **`customer-service`**: `"state"` is `CLOSED`, `DISABLED`, `OPEN`, etc.
- `GET http://localhost:9070/actuator` — JSON index lists which endpoints are exposed; open the **retry**-related one (often `retryevents` or `retries`, depending on Resilience4j version) to see **`customer-service-retry`** events (`type`: `SUCCESS`, `RETRY`, …) when retries fire.

Configure Postman timeouts **above** (~5s+) when simulating hangs so you can see stalled requests.

**Configuration note:** Retry and circuit breaker **`ignoreExceptions`** for business-style failures (`InvalidCustomerException`, `NonRetryableSagaException` hierarchy, etc.) live in [`application.yaml`](../application.yaml) under `resilience4j.retry` / `resilience4j.circuitbreaker` so those outcomes do **not** count toward opening the breaker and are **not** retried.

---

## 1. Retry — HTTP failures are retried; business failures are not

**1a — Downstream unreachable (no breakpoint)**

1. Ensure Customer is **stopped** or not listening on `:9060`.
2. Enable DEBUG logs for Order (`logging.level.com.ab.ms.order: DEBUG`; Feign traces help).
3. Send **one** `POST http://localhost:9070/api/orders` with a valid-shaped body.
4. **Observe**
   - Order logs: multiple outbound attempts spaced by **`resilience4j.retry.instances.customer-service-retry.waitDuration`** (e.g. 300ms).
   - Retry actuator (see index above): **`RETRY`** entries for **`customer-service-retry`** (not only a single terminal failure).

**Expected:** Up to **`maxAttempts`** total tries (YAML `customer-service-retry.maxAttempts`; e.g. 4 ⇒ one initial plus three backoff gaps). Eventually **502/500** depending on exception mapping (often Feign unwraps to a generic handler error).

**1b — Breakpoint: simulate “nonresponsive” downstream**

Use this when you want Customer **up** but the call **never completes**.

1. In **Customer service**, set a breakpoint in the REST handler that serves **`GET /api/customers/{id}/status`** (controller or service invoked by that endpoint), **before** it returns — so the HTTP response is never produced while Execution is paused.
2. Attach debugger; start Customer debug on its port (**9060**).
3. From Postman, `POST http://localhost:9070/api/orders` with a **`customerId` that routes to your breakpoint**.
4. **Observe**
   - First attempt blocks until Order’s Feign **read/connect** timeout (short values in `spring.cloud.openfeign.client.config.customer-service`: e.g. 200ms cause quick timeout).
   - Order logs + retry events show **another attempt** each time reads time out (`SocketTimeoutException` / Feign wrappers are in **`retryExceptions`**).

**Expected:** Same retry pattern as 1a, but confirms retries on **timeouts** while Customer process is alive.

**1c — Business invariant: invalid / inactive customer (no retry)**

1. Customer running and returning **HTTP 200** with `exists:false` or `active:false` for a given id (depends on Customer API semantics), or whatever makes Order throw **`InvalidCustomerException`** after the Feign decode.
2. Send **one** `POST .../orders` using that **`customerId`**.

**Expected:** Exactly **one** Feign/status call in logs (**no** repeating retry gap). HTTP **422** (`UNPROCESSABLE_ENTITY`) via `GlobalExceptionHandler`. Retry actuator: no meaningful **RETRY** burst for this call.

---

## 2. Circuit breaker — opens after sustained failures

Config reference: **`resilience4j.circuitbreaker.instances.customer-service`** (`slidingWindowSize`, `minimumNumberOfCalls`, `failureRateThreshold`, `waitDurationInOpenState`).

**Steps**

1. Keep Customer **down** (or unreachable), so each Feign invocation fails immediately (aligns well with accumulating failures quickly).
2. Send **`POST .../orders` repeatedly** until the sliding window crosses the breaker rules (with defaults like `minimumNumberOfCalls: 3`, `failureRateThreshold: 50`, short windows—often takes **several** quick requests).
3. **Observe** `GET /actuator/circuitbreakers`:
   - Instance **`customer-service`** moves to **`OPEN`** once failure rate rules apply.
4. While **OPEN**, send **one more** valid `POST .../orders`.

**Expected:** Fast failure (**503 SERVICE_UNAVAILABLE**) with body from `GlobalExceptionHandler` for **`CallNotPermittedException`**, without waiting for Customer or full Feign timeout. Subsequent calls while open stay fast-failed similarly.

---

## 3. Recovery — half-open and close

1. After **`OPEN`**, leave Customer **stopped** briefly, then **`waitDurationInOpenState`** elapses (e.g. **10s**).
2. **Start Customer** healthy.
3. Send **`POST .../orders`** with a **valid** active customer id and catalog item.

**Expected:** **`/actuator/circuitbreakers`** for **`customer-service`** returns **`CLOSED`** (possibly via **`HALF_OPEN`** first). **`201`** when validation and order creation succeed.

---

## Quick checklist

| Scenario                              | Retry events / log pattern | Typical HTTP outcome         |
|---------------------------------------|----------------------------|------------------------------|
| Customer down / connection error      | Multiple attempts          | Error after exhaustion       |
| Customer hung (breakpoint + timeout)  | Multiple read timeouts     | Same                         |
| Invalid customer after 200 OK         | Single attempt             | **422**                      |
| Breaker OPEN                          | CB short-circuit           | **503** (`CallNotPermitted`) |

Adjust **`maxAttempts`** and **`waitDuration`** in YAML to make retries obvious in logs and actuator without making tests painfully slow.
