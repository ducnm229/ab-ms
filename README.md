Microservices Architecture Playground
====================================================
Repository containing multiple Spring Boot microservice systems built to explore distributed systems architecture, resiliency patterns, event-driven communication, observability, and containerized deployment workflows.

Tech Stack
--------------------------------
* Java 17/21
* Spring Boot 3
* Spring Cloud Config
* Eureka Service Discovery
* Spring Cloud Gateway
* Spring Security + Keycloak
* Resilience4j
* Kafka / RabbitMQ
* Spring Cloud Stream
* Docker
* Kubernetes (kind)
* Helm / Headlamp UI
* Grafana / Loki / Prometheus / Tempo

Banking Domain Services
--------------------------------
Infrastructure-focused microservice system used to explore cloud-native platform concerns and operational tooling.

** Services
* Account Service
* Loan Service
* Card Service
* Message Service

** Implemented Infrastructure
* Spring Cloud Config Server
* Eureka Service Discovery
* API Gateway routing
* OAuth2/OIDC security with Keycloak
* Synchronous and asynchronous communication
* Resilience4j fault-tolerance patterns
* Centralized logging and metrics
* Distributed tracing
* Kubernetes deployment using Helm charts

** Observability Stack
* Grafana
* Loki
* Prometheus
* Tempo

Ecommerce Domain Services
--------------------------------
Workflow-focused microservice system used to explore distributed transaction patterns and asynchronous coordination.

** Services
* Customer Service
* Order Service
* Inventory Service
* Payment Service

** Implemented Workflows
* Orchestrated saga-based order processing
* Inventory reservation and compensation flow
* Kafka-based event-driven communication
* Retry and circuit breaker handling on remote calls
* Local deployment via Docker Compose

** Saga transition table
| Current State      | Event             | Next State         | Commands         |
| ------------------ | ----------------- | ------------------ | ---------------- |
| CREATED            | OrderCreated      | PENDING            | ReserveInventory |
| PENDING            | InventoryReserved | INVENTORY_RESERVED | ProcessPayment   |
| PENDING            | InventoryFailed   | CANCELLED          | —                |
| INVENTORY_RESERVED | PaymentSucceeded  | PAYMENT_COMPLETED  | ConfirmOrder     |
| INVENTORY_RESERVED | PaymentFailed     | CANCELLING         | ReleaseInventory |
| CANCELLING         | InventoryReleased | CANCELLED          | —                |
| PAYMENT_COMPLETED  | OrderConfirmed    | CONFIRMED          | —                |

Current Scope / Future Improvements
--------------------------------
The ecommerce services currently implement a simplified orchestration saga with basic idempotency and concurrency protections, including:

* Pessimistic locking during inventory reservation
* Idempotent payment constraints via partial unique indexes
* Defensive state-transition handling for out-of-order events

TODO - things to do for more production-like quality
--------------------------------
* Transactional Outbox pattern
* Inbox/consumer deduplication pattern
* Dead Letter Queue (DLQ) handling
* Exactly-once/event replay strategies
* Workflow recovery and reconciliation mechanisms
