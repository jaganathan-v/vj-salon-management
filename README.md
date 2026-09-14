# ✂ VIJAYAN SALON MANAGEMENT SYSTEM (VJ_Salon_Management)

**A Premium Full-Stack Salon Management System built with Spring Boot 3.2, Java 17, PostgreSQL 16, and Vanilla Modern Frontend with Ink & Ember Design.**

---

## 📌 1. WHERE TO CHANGE CODE FOR DATABASE CONNECTIVITY

To connect the application to your local or remote **PostgreSQL** database, open the following file:

📁 **`backend/src/main/resources/application.properties`**

Modify lines **9 to 11**:

```properties
# 1. Database URL (Change 'vjsalon_db' to your PostgreSQL database name)
spring.datasource.url=jdbc:postgresql://localhost:5432/vjsalon_db

# 2. Database Username (Default: postgres)
spring.datasource.username=postgres

# 3. Database Password (Enter your PostgreSQL password)
spring.datasource.password=yourpassword
```

> [!TIP]
> If you create a new PostgreSQL database named `vjsalon_db` in PostgreSQL (e.g. via pgAdmin, DBeaver, or psql with `CREATE DATABASE vjsalon_db;`), Spring Boot will **automatically create all tables and populate initial seed data** on its very first run via Hibernate `ddl-auto=update` and the built-in `DataSeeder`!

---

## 🗄️ 2. WHAT IS IN THE DATABASE (PostgreSQL Schema)

The database schema (`database/schema.sql`) contains **13 structured tables**:

| Table Name | Purpose / Contents |
|---|---|
| `shop_settings` | Stores shop profile: Name (`VIJAYAN SALON`), Tagline, Salem Address, Phone (`6374402014`), Since Year (2010), Google Maps link. |
| `shop_status` | Real-time Open/Closed boolean state, open/close timestamps, and announcement note. |
| `admin_config` | Admin ID code (`VJADMIN`), BCrypt-hashed password, and contact email. |
| `stylists` | Stylist accounts (`stylist_code`, `name`, hashed password, status: `FREE` / `BUSY` / `FOOD_BREAK`, active flag). |
| `services` | Service catalog with prices, discount offers, category (`HAIR`, `BEARD`, `SKIN`), and multilingual names (`name_en`, `name_ta`, `name_hi`). |
| `bookings` | Customer appointments with name, 10-digit mobile, selected services, date, 30-min time slot, and status (`PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`). |
| `feedback` | 5-dimension customer ratings (Service 1-5★, Cleanliness 1-5★, Staff 1-5★, Timing 1-5★, Overall 1-5★) and text comments. |
| `daily_log` | Stylist daily completed service entries (+1 Cut, +1 Shave), payment method (`CASH` vs `ONLINE` UPI), amount, and log date. |
| `inventory` | Shop supplies counter (`item_name`, category, `current_count`, `unit`, `low_threshold` alert count). |
| `advertisements` | Media records for images & video advertisements displayed in the frontpage carousel. |
| `events` | Special daily notices and festival banners shown to customers on that day. |
| `achievements` | Shop milestones and awards timeline (Best Salon Salem 2018, 500+ Clients 2020, Google 4.8★ 2023). |
| `offers` | Promotional discounts (Festival 20% OFF, Combo discounts) with validity dates. |
| `payment_qr` | Google Pay / UPI QR Code image path (`assets/uploads/qr/payment_qr.jpg`), UPI ID (`6374402014@okbizaxis`), and display name (`VIJAYAN SALOON`). |

---

## 🚀 3. HOW TO RUN THE APPLICATION EFFICIENTLY

### Option A: Standalone Instant Run (Frontend Only — Zero Setup Needed)
The frontend is built to work **immediately** with built-in fallback demo data and local storage simulation:
1. Open Chrome or any web browser.
2. Open `frontend/index.html` directly in the browser!
3. Navigate between **Client**, **Stylist**, **Admin**, and **Settings** portals smoothly.

### Option B: Full Stack Run (Spring Boot Backend + PostgreSQL Database)

#### Step 1 — Create the Database in PostgreSQL:
In `psql` or `pgAdmin`:
```sql
CREATE DATABASE vjsalon_db;
```
*(Optional)* You can run `database/schema.sql` if you wish to pre-seed via SQL, or let Spring Boot auto-seed on startup.

#### Step 2 — Start the Spring Boot Backend:
Open terminal in the `backend` directory:
```bash
cd backend
mvn clean spring-boot:run
```
Backend will start on `http://localhost:8080`.

#### Step 3 — Open the Frontend:
Open `frontend/index.html` in your browser. All API calls will automatically connect to `http://localhost:8080/api`.

---

## 🔐 4. DEFAULT USER LOGIN CREDENTIALS

### Master Admin Portal (`admin.html`):
- **Admin Code:** `VJADMIN`
- **Password:** `vj@admin2024`
- *(Can be changed anytime inside the Admin Portal under "Shop & Admin Pwd" tab; changes update directly in PostgreSQL)*

### Stylist Staff Portal (`stylist.html`):
- **Stylist 1:** `VJS001` / Password: `stylist1` (Vijayan)
- **Stylist 2:** `VJS002` / Password: `stylist2` (Kumar)
- *(Admin can create unlimited new stylist accounts or reset passwords in Admin -> Stylists tab)*

---

## 🖥️ 5. PAGE-BY-PAGE FEATURE BREAKDOWN

### 1. Frontpage (`index.html`)
- **Live Clock:** Real-time digit flip clock showing current time, date, and year.
- **Shop Status Pill:** Live indicator showing whether shop is currently Open or Closed.
- **Hero Section:** "Where Every Cut Tells a Story" with scissor motifs and quick action buttons.
- **Marquee:** Infinite scrolling bar of available services.
- **Ad Carousel:** Visual carousel displaying shop achievements and promotions.
- **Offers Section:** Active discount cards with countdown validity.
- **Stylists Availability:** Live status cards showing which stylists are Free, Busy, or on Food Break.
- **Services Catalog:** Grouped by Hair, Beard, and Skin with prices in ₹.
- **Shop Info & Map:** Embedded Google Map of Ponnammapet Salem, full address, clickable phone link, and since 2010 copyright footer.

### 2. Client Portal (`client.html`)
- **Shop Status View:** Shows if shop is open and stylist availability.
- **3-Step Booking Wizard:**
  1. *Services Selection:* Multi-select services with live total calculation.
  2. *Client Details:* Name, 10-digit mobile, date picker, 30-min time slot, notes.
  3. *Confirmation:* Generates booking reference number (`VJ-XXXXXX`) and notifies stylist.
- **5-Star Rating Feedback Form:** Service quality, cleanliness, staff behaviour, time punctuality, and overall rating.
- **Online Payment Portal:** Reveals the official **Google Pay / UPI QR Code** (`6374402014@okbizaxis`), one-click copy UPI button, and supported UPI apps.

### 3. Stylist Portal (`stylist.html`)
- **Login Gate:** Secure stylist ID + password authentication.
- **Shop Open/Close Switch:** Stylist can toggle shop open/close state with an optional reason note.
- **My Status Selector:** 3 live status buttons — *Free & Available* (🟢), *With Client (Busy)* (🔴), *Food/Tea Break* (🟡).
- **Booking Notifications:** Real-time incoming customer appointments with Confirm / Complete / Cancel controls.
- **Daily Service Logger:** Log finished cuts (+1 Haircut, +1 Shave) with payment method (**Hand Cash** vs **Online UPI**).
- **Inventory Counter:** View supply counts, decrement counter on use (`−` button), add new items, and receive low-stock alerts.

### 4. Admin Portal (`admin.html`)
- **Master Login:** Secure Admin ID + Password login.
- **Executive Dashboard:** Today's revenue, appointments count, satisfaction score, and stylist performance.
- **Data Analytics (Chart.js):** Visual charts for Daily, Weekly, Monthly, and Yearly revenue, service popularity donut chart, stylist work count bar chart, and rating trends.
- **Full CRUD Management:**
  - *Services:* Add/edit service names (English, Tamil, Hindi), prices, categories, and discounts.
  - *Stylists:* Create worker logins, reset passwords, deactivate accounts.
  - *Bookings:* Filter, confirm, complete, delete, and **Export to CSV**.
  - *Feedback:* View all 5-dimension ratings and comments.
  - *Offers & Events:* Publish promotional discounts and day notices.
  - *Payment QR:* Upload replacement QR code image and update UPI ID.
  - *Shop Settings:* Modify shop address, phone, and update Admin password in database.
- **Access All Portals:** Direct quick-links to Frontpage, Client, and Stylist views.

### 5. Settings Page (`settings.html`)
- **Theme Switcher:** 3 distinct themes — *Ink & Charcoal (Dark)*, *Clean Ivory (Light)*, *Warm Sepia (Vintage)*.
- **Multilingual Switcher:** English (🇬🇧), Tamil (🇮🇳 தமிழ்), Hindi (🇮🇳 हिंदी).
- **Text Sizing:** Small (14px), Standard (15px), Large (17px).
- **User-Isolated Storage:** All preferences are stored in the client's `localStorage` so they apply **only to that current user's browser**.
- **Reset Preferences:** One-click reset to default settings.

---

## 🎨 6. DESIGN SYSTEM — "INK & EMBER"

- **Color Palette:**
  - Deep Void Charcoal (`#08080F`)
  - Burnt Amber Accent (`#D9722D` / `#FFB870`)
  - Slate Teal Secondary (`#267878` / `#34AAAA`)
  - Warm Cream Text (`#F0E8D5`)
- **Typography:** *Playfair Display* (luxury headings) + *Plus Jakarta Sans* (clean readable body).
- **Design Motifs:** Barber pole CSS animation, subtle geometric grid overlay, pill status dots with pulse animations, glassmorphism cards.
