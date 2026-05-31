
# Cafe Management System
## Student 1 — Imman | Module 1: Authentication & Dashboard

### Initial Commit — Project Foundation

---

## What Imman Did

Imman created the **entire project from scratch**. This is the first commit.
She set up the project structure, all shared infrastructure, and built Module 1 (Authentication & Dashboard).

Every other student cloned this and built on top of it.

---

## Files Created in This Commit

### Shared Infrastructure (used by all modules)
```
src/com/cafe/database/
  DBConnection.java          ← connects to MySQL, used by ALL services

src/com/cafe/models/
  Customer.java              ← customer data (name, phone, email, address)
  MenuItem.java              ← menu item (name, category, price, available)
  Order.java                 ← one order (customer + items + total)
  OrderItem.java             ← one item inside an order (qty, price, total)
  Bill.java                  ← generates formatted bill text

src/com/cafe/utils/
  UIUtils.java               ← colors, fonts, button styles (used by all panels)
  MessageUtils.java          ← popup messages (error, success, confirm)
  ValidationUtils.java       ← input validation helpers
  DateTimeUtils.java         ← date formatting

src/com/cafe/services/
  BaseService.java           ← abstract CRUD contract (all services extend this)

database/
  cafe_management.sql        ← full database schema + sample data + users table
```

### Module 1 — Imman's Own Work
```
src/com/cafe/utils/
  SessionManager.java        ← stores who is logged in (global static)

src/com/cafe/services/
  AuthService.java           ← checks username/password against users table
  DashboardService.java      ← counts customers, orders, revenue for dashboard

src/com/cafe/gui/
  LoginPanel.java            ← login screen (JFrame) — app entry point
  MainDashboard.java         ← main window with CardLayout + sidebar navigation
  DashboardPanel.java        ← home screen with 4 summary cards + recent orders
```

---

## How to Run This Version

1. Open XAMPP → Start MySQL
2. Open phpMyAdmin → Import `database/cafe_management.sql`
3. Open project in NetBeans
4. Add `mysql-connector-j.jar` to project libraries
5. Right-click `LoginPanel.java` → Run File
6. Login: `admin` / `admin123`

**What you'll see:** Login screen → Dashboard with summary cards.
Sidebar has only "Dashboard" working. Other buttons are placeholders until other students add their modules.

---


## What's NOT in This Version Yet

- No menu management (Zainab adds this in Student 2)
- No order placement (Fatima adds this in Student 3)
- No sales reports (GulNaaz adds this in Student 4)

---

## Database Tables Created

```sql
users        ← login accounts (admin/admin123, staff1/staff123)
customers    ← customer records (sample data included)
menu_items   ← food/drink items (8 sample items)
orders       ← order headers
order_items  ← items within each order
```

---

## Key Technical Decisions

**Why CardLayout?**
The university requires a single integrated window. CardLayout lets all 4 modules
share one window — switching panels without opening new windows.

**Why BaseService<T>?**
Provides a common contract for CRUD operations, making the codebase extensible and maintainable.
Defines a common CRUD contract. Every service must implement add(), update(), delete(),
search(), getAll(). Enforces consistency across all modules.

**Why SessionManager as static?**
After login, any module needs to know who is logged in. A static class provides
one global copy accessible from anywhere without passing objects around.

**Why PreparedStatement everywhere?**
Prevents SQL injection. All `?` placeholders are filled safely by JDBC.

---

## Login Credentials
```
Username: admin     Password: admin123   (role: admin)
Username: staff1    Password: staff123   (role: staff)
```
