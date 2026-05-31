# Cafe Management System
## Student 2 — Zainab | Module 2: Menu & Inventory Management

### Second Commit — Menu CRUD + NetBeans Drag-and-Drop Inventory Form

---

## What Zainab Did

Zainab **cloned Imman's project** and added Module 2 on top of it.
She built the complete menu management screen and also created an inventory form
using **NetBeans drag-and-drop GUI designer** (the `.form` file approach).

---

## New Files Added in This Commit

```
src/com/cafe/services/
  MenuService.java           ← full CRUD for menu items (6 methods)

src/com/cafe/gui/
  MenuPanel.java             ← menu management screen (JPanel with JTable + form)

Inventory.java               ← NetBeans drag-and-drop inventory form (JFrame)
Inventory.form               ← NetBeans Form Editor file (visual designer data)
```

---

## What Changed vs Student 1

| What | Student 1 (Imman) | Student 2 (Zainab) |
|------|-------------------|------------------|
| Menu management | Not present | ✅ Full CRUD screen |
| Inventory form | Not present | ✅ NetBeans drag-and-drop form |
| Sidebar navigation | Dashboard only | ✅ + Menu & Inventory button |
| MenuService | Only `getAvailableItems()` | ✅ Full 6-method service |

---

## About Inventory.java and Inventory.form

These two files are created by **NetBeans Form Editor** (drag-and-drop GUI designer).

**How it works:**
1. In NetBeans: right-click package → New → JFrame Form
2. Drag components (JLabel, JTextField, JButton, JTable) onto the canvas
3. NetBeans generates two files:
   - `Inventory.form` — XML file storing the visual layout (don't edit manually)
   - `Inventory.java` — Java code generated from the form (the `initComponents()` method)

**What the Inventory form contains:**
- A JTable (`table_inventory`) with columns: ID, Item Name, Price, Quantity
- Three text fields for entering item name, price, quantity
- Three buttons: Add Item, Update, Delete

**Why use drag-and-drop?**
For simple forms, NetBeans Form Editor is faster than writing layout code manually.
The generated `initComponents()` method handles all the layout automatically.

**Important:** The `Inventory.form` file must stay alongside `Inventory.java`.
NetBeans uses the `.form` file to re-open the visual designer.

---

## MenuService.java — 6 Methods

| Method | SQL | Purpose |
|--------|-----|---------|
| `addItem(item)` | INSERT | Add new menu item |
| `updateItem(item)` | UPDATE | Edit existing item |
| `deleteItem(id)` | DELETE | Remove item |
| `getAllItems()` | SELECT * | Load all for table |
| `search(keyword)` | SELECT WHERE LIKE | Search by name/category |
| `getAvailableItems()` | SELECT WHERE available=TRUE | Used by Module 3 |

---

## MenuPanel.java — How It Works

1. Opens → loads all menu items into JTable
2. User clicks a row → form fields fill automatically (ListSelectionListener)
3. User edits fields → clicks Update or Delete
4. User fills empty form → clicks Add Item
5. Search bar → filters table in real time

---

## How to Run This Version

Same as Student 1, but now:
- Click **"MN  Menu & Inventory"** in the sidebar
- You can add, edit, delete, search menu items
- The Inventory form can be opened separately from `Inventory.java`

---

## Integration Point for Module 3

`MenuService.getAvailableItems()` is called by Fatima's `OrderPanel` to populate the order menu.
the item dropdown. If Zainab Raza marks an item unavailable, it won't appear in orders.

---

## What's NOT in This Version Yet

- No order placement (Usman adds this in Student 3)
- No sales reports (Zainab adds this in Student 4)
