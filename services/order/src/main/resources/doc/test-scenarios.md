TEST SCENARIOS:
Scenario 1: Success path (complete to CONFIRMED using manual final event)
Create order
POST {{orderBaseUrl}}/api/orders
Body:

{
  "customerId": 1001,
  "items": [
    { "productId": "K100", "quantity": 2, "price": 50.00 },
    { "productId": "K200", "quantity": 1, "price": 20.00 }
  ]
}
Save id as orderId.

Poll order status
GET {{orderBaseUrl}}/api/orders/{{orderId}} every 1-2s until it reaches PAYMENT_COMPLETED (or CONFIRMED if you later add consumer).

Manually finalize confirm event
POST {{orderBaseUrl}}/api/orders/saga-events
Body:

{
  "event": {
    "type": "OrderConfirmed",
    "orderId": "{{orderId}}"
  }
}
Verify final status
GET {{orderBaseUrl}}/api/orders/{{orderId}} -> expect CONFIRMED.

Verify inventory reduced
GET {{inventoryBaseUrl}}/inventory/K100 and K200 -> quantities decreased.

Scenario 2: Inventory failure -> CANCELLED
Create order with too-high quantity: POST {{orderBaseUrl}}/api/orders

{
  "customerId": 1002,
  "items": [
    { "productId": "K100", "quantity": 999999, "price": 1.00 }
  ]
}
Poll GET /api/orders/{{orderId}} until status settles.

Expect final status: CANCELLED (inventory command fails, no payment step).

Verify stock unchanged: GET {{inventoryBaseUrl}}/inventory/K100.

Scenario 3: Payment failure compensation (inventory release)
This can happen naturally (payment service uses deterministic UUID rule), but for deterministic Postman testing use manual event injection after inventory is reserved.

Create a normal order (small quantities).
Poll until status is INVENTORY_RESERVED.
Trigger payment fail manually: POST {{orderBaseUrl}}/api/orders/saga-events
{
  "event": {
    "type": "PaymentFailed",
    "orderId": "{{orderId}}",
    "reason": "manual-test"
  }
}
Poll order until CANCELLED.
Verify inventory restored to original quantity with GET /inventory/{productId}.
Scenario 4: Direct inventory controller sanity checks
Useful to validate inventory behavior independently:

POST {{inventoryBaseUrl}}/inventory/reserve
{ "orderId": "11111111-1111-1111-1111-111111111111", "productId": "K300", "quantity": 10 }
GET {{inventoryBaseUrl}}/inventory/K300 -> reduced.
POST {{inventoryBaseUrl}}/inventory/release
{ "orderId": "11111111-1111-1111-1111-111111111111" }
GET {{inventoryBaseUrl}}/inventory/K300 -> restored.