# Order Saga Workflow

## Overview
This document defines details of the Order Saga workflow, including triggering events and resulting commands.

---

## Transition Table

states:
  - CREATED
  - PENDING
  - INVENTORY_RESERVED
  - PAYMENT_COMPLETED
  - CANCELLING
  - CANCELLED
  - CONFIRMED

transitions:
  - from: CREATED
    event: OrderCreated
    to: PENDING
    commands:
      - ReserveInventory

  - from: PENDING
    event: InventoryReserved
    to: INVENTORY_RESERVED
    commands:
      - ProcessPayment

  - from: PENDING
    event: InventoryFailed
    to: CANCELLED
    commands: []

  - from: INVENTORY_RESERVED
    event: PaymentSucceeded
    to: PAYMENT_COMPLETED
    commands:
      - ConfirmOrder

  - from: INVENTORY_RESERVED
    event: PaymentFailed
    to: CANCELLING
    commands:
      - ReleaseInventory

  - from: CANCELLING
    event: InventoryReleased
    to: CANCELLED
    commands: []

  - from: PAYMENT_COMPLETED
    event: OrderConfirmed
    to: CONFIRMED
    commands: []

| Current State      | Event             | Next State         | Commands         |
| ------------------ | ----------------- | ------------------ | ---------------- |
| CREATED            | OrderCreated      | PENDING            | ReserveInventory |
| PENDING            | InventoryReserved | INVENTORY_RESERVED | ProcessPayment   |
| PENDING            | InventoryFailed   | CANCELLED          | —                |
| INVENTORY_RESERVED | PaymentSucceeded  | PAYMENT_COMPLETED  | ConfirmOrder     |
| INVENTORY_RESERVED | PaymentFailed     | CANCELLING         | ReleaseInventory |
| CANCELLING         | InventoryReleased | CANCELLED          | —                |
| PAYMENT_COMPLETED  | OrderConfirmed    | CONFIRMED          | —                |



