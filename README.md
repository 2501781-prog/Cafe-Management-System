

# Cafe Management System — Final Documentation

Professional Java OOP desktop project using **Java Swing** and **MySQL JDBC**.

---

## Table of Contents
- [Project Overview](#project-overview)
- [Requirements](#requirements)
- [Setup & Installation](#setup--installation)
- [Modules & Contributors](#modules--contributors)
- [Features](#features)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [GitHub Push Order](#github-push-order)
- [Common Issues & Fixes](#common-issues--fixes)

---

## Project Overview
This Cafe Management System is a modular Java desktop application for managing authentication, menu/inventory, orders, billing, and sales analytics. It is designed for educational purposes, demonstrating OOP, GUI, and database integration.

---

## Requirements
- **Java JDK 17+**
- **Apache NetBeans 21** (or compatible IDE)
- **XAMPP** (MySQL & Apache)
- **MySQL Connector/J** (already included in `lib/`)

---

## Setup & Installation
1. Install XAMPP and start MySQL and Apache.
2. Open phpMyAdmin at [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
3. Import the SQL schema: `database/cafe_management.sql`.
4. Open the project folder in NetBeans (preferably Student4_GulNaz_Billing_Reports for the final version).
5. Ensure `lib/mysql-connector-j-9.7.0.jar` is added to project libraries.
6. Update database credentials in `src/com/cafe/database/DBConnection.java` if needed.
7. Clean and Build, then Run Project (F6).
8. Login with: `admin` / `admin123`.

---

## Modules & Contributors
- **Student 1 — Imman**: Authentication & Dashboard ([Student1_Imman_Auth_Dashboard/](Student1_Imman_Auth_Dashboard/))
- **Student 2 — Zainab**: Menu & Inventory Management ([Student2_Zainab_Menu_Inventory/](Student2_Zainab_Menu_Inventory/))
- **Student 3 — Fatima**: Order Management ([Student3_Fatima_Order_Management/](Student3_Fatima_Order_Management/))
- **Student 4 — GulNaaz**: Billing & Reports (Sales Analytics) ([Student4_GulNaz_Billing_Reports/](Student4_GulNaz_Billing_Reports/))

---

## Features
- User authentication (admin login)
- Dashboard with summary cards (customers, orders, revenue, menu items)
- Menu & inventory CRUD (add, update, delete, search)
- Customer management (CRUD, search)
- Order placement with cart, item selection, and bill generation
- Sales analytics: revenue, top items, category breakdown, custom bar chart
- Secure admin-only access to reports
- Modular OOP design (encapsulation, inheritance, abstraction, polymorphism)
- All SQL operations use PreparedStatement for security

---

## Project Structure
- `src/com/cafe/gui/` — GUI panels and dialogs
- `src/com/cafe/models/` — Domain classes
- `src/com/cafe/database/` — JDBC connection
- `src/com/cafe/services/` — Service/DAO layer
- `src/com/cafe/utils/` — Utilities
- `database/` — SQL schema and sample data
- `lib/` — MySQL Connector/J

---

## How to Run
1. Open XAMPP → Start MySQL and Apache
2. Open phpMyAdmin → Import `database/cafe_management.sql`
3. Open the project in NetBeans (preferably Student4_GulNaz_Billing_Reports)
4. Add `mysql-connector-j-9.7.0.jar` to project libraries if not already present
5. Clean and Build, then Run Project (F6)
6. Login: `admin` / `admin123`

---

## Common Issues & Fixes
- **MySQL JDBC Driver not found**: Add MySQL Connector/J JAR in NetBeans project libraries.
- **Access denied for user root**: Update username/password in `DBConnection.java`.
- **Unknown database**: Run `database/cafe_management.sql`.
- **Communications link failure**: Start MySQL server and confirm port `3306`.
- **Cannot delete customer**: Customer has existing orders; delete linked orders first or keep the customer for history.

---

## Credits
This project was developed as a university OOP lab project by Imman, Zainab, Fatima, and GulNaaz.
