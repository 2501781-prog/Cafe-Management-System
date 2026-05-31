
# Cafe Management System
## Student 3 — Fatima | Module 3: Order Management

### Third Commit — Customer CRUD + Cart-Based Order Placement + Bill Generation

---


## What Fatima Did

Fatima **cloned Zainab's project** (which already had Imman's base + Zainab's menu module)
and added Module 3 on top of it.

She built the complete ordering workflow: customer management, cart-based order placement
with running total, database transaction handling, and bill generation.

---

## New Files Added in This Commit

```
src/com/cafe/services/
  CustomerService.java       ← customer CRUD and search
  OrderService.java          ← saves orders using database transactions

src/com/cafe/gui/
  CustomerPanel.java         ← customer management screen (CRUD + search)
  OrderPanel.java            ← order screen with cart, item selection, total
  BillDialog.java            ← bill preview popup (JDialog)
```

---

## What Changed vs Student 2

| What | Student 2 (Zainab) | Student 3 (Fatima) |
|------|------------------|-------------------|
| Customer management | Not present | ✅ Full CRUD screen |
| Order placement | Not present | ✅ Cart-based ordering |
| Bill generation | Not present | ✅ BillDialog popup |
| Sidebar navigation | Dashboard + Menu | ✅ + Customers + Orders |
| Database transactions | Not used | ✅ setAutoCommit/commit/rollback |

---

## The Most Important Feature: Database Transactions

When placing an order, Usman saves to TWO tables atomically:

```java
conn.setAutoCommit(false);  // start transaction

// 1. INSERT into orders → get auto-generated order_id
// 2. INSERT all order_items using that order_id (batch insert)

conn.commit();    // save both together
// OR
conn.rollback();  // if anything fails, undo everything
```

**Why this matters:** Without a transaction, if step 1 succeeds but step 2 fails,
you'd have an order with no items — corrupted data. The transaction guarantees
either everything saves or nothing does (Atomicity).

---

## OrderPanel.java — How the Cart Works

1. Staff selects a customer from dropdown
2. Selects a menu item (loaded from Zainab's `MenuService.getAvailableItems()`)
3. Sets quantity with JSpinner (min=1, max=100)
4. Clicks "Add Item" → item goes into `List<OrderItem>` in memory
5. Running total updates automatically (`lineTotal = unitPrice × quantity`)
6. Clicks "Place Order" → `OrderService.add(order)` saves everything to DB
7. `BillDialog` opens showing the formatted receipt

---

## CustomerPanel.java — Customer CRUD

- Add new customers (name, phone, email, address)
- Update existing customer details
- Delete customers (blocked if they have orders — foreign key protection)
- Search by name, phone, or email

---

## BillDialog.java — Bill Preview

A modal `JDialog` that shows a formatted text receipt:
```
CAFE MANAGEMENT SYSTEM
Bill No: 5
Date: 25-May-2026 02:30 PM
Customer: Ali Khan
Phone: 03001234567
----------------------------------------
Item               Qty    Price    Total
Cappuccino           1   450.00   450.00
Club Sandwich        1   650.00   650.00
----------------------------------------
Grand Total:                    1100.00
----------------------------------------
Thank you. Please visit again.
```

---

## How to Run This Version

Same as Student 2, but now:
- Click **"CU  Customers"** to manage customers
- Click **"OR  Orders & Billing"** to place orders
- Select customer → add items to cart → Place Order → see bill

---

## Integration Points

**Uses Module 2:** `menuService.getAvailableItems()` loads items for the cart.
**Feeds Module 4:** Usman's orders are what Zainab's reports read from.

---

## What's NOT in This Version Yet

- No sales reports (Zainab adds this in Student 4)
