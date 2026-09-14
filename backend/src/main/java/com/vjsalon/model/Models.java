package com.vjsalon.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Models {

    @Entity
    @Table(name = "shop_settings")
    public static class ShopSettings {
        @Id
        private Long id = 1L;
        @Column(length = 100)
        private String shopName = "VIJAYAN SALON";
        @Column(length = 200)
        private String tagline;
        @Column(columnDefinition = "TEXT")
        private String address;
        @Column(length = 20)
        private String phone;
        @Column(length = 100)
        private String email;
        private Integer sinceYear = 2010;
        @Column(columnDefinition = "TEXT")
        private String mapsLink;

        public ShopSettings() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
        public String getTagline() { return tagline; }
        public void setTagline(String tagline) { this.tagline = tagline; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Integer getSinceYear() { return sinceYear; }
        public void setSinceYear(Integer sinceYear) { this.sinceYear = sinceYear; }
        public String getMapsLink() { return mapsLink; }
        public void setMapsLink(String mapsLink) { this.mapsLink = mapsLink; }
    }

    @Entity
    @Table(name = "shop_status")
    public static class ShopStatus {
        @Id
        private Long id = 1L;
        private boolean isOpen = false;
        private LocalDateTime openedAt;
        private LocalDateTime closedAt;
        @Column(length = 255)
        private String note;

        public ShopStatus() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public boolean isOpen() { return isOpen; }
        public void setOpen(boolean open) { isOpen = open; }
        public LocalDateTime getOpenedAt() { return openedAt; }
        public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }
        public LocalDateTime getClosedAt() { return closedAt; }
        public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    @Entity
    @Table(name = "stylists")
    public static class Stylist {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(unique = true, nullable = false, length = 20)
        private String stylistCode;
        @Column(nullable = false, length = 100)
        private String name;
        @Column(nullable = false)
        private String passwordHash;
        @Column(length = 20)
        private String status = "FREE"; // FREE, BUSY, FOOD_BREAK
        private boolean active = true;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Stylist() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getStylistCode() { return stylistCode; }
        public void setStylistCode(String stylistCode) { this.stylistCode = stylistCode; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @Entity
    @Table(name = "admin_config")
    public static class AdminConfig {
        @Id
        private Long id = 1L;
        @Column(nullable = false, length = 50)
        private String adminCode = "VJADMIN";
        @Column(nullable = false)
        private String passwordHash;
        @Column(length = 100)
        private String email;

        public AdminConfig() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getAdminCode() { return adminCode; }
        public void setAdminCode(String adminCode) { this.adminCode = adminCode; }
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @Entity
    @Table(name = "services")
    public static class SalonService {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false, length = 100)
        private String nameEn;
        @Column(length = 100)
        private String nameTa;
        @Column(length = 100)
        private String nameHi;
        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal price;
        private Integer offerPct = 0;
        @Column(length = 50)
        private String category = "HAIR"; // HAIR, BEARD, SKIN, OTHER
        @Column(columnDefinition = "TEXT")
        private String description;
        private boolean active = true;
        private Integer displayOrder = 0;

        public SalonService() {}
        public SalonService(String nameEn, String nameTa, String nameHi, BigDecimal price, Integer offerPct, String category, String description, Integer displayOrder) {
            this.nameEn = nameEn;
            this.nameTa = nameTa;
            this.nameHi = nameHi;
            this.price = price;
            this.offerPct = offerPct;
            this.category = category;
            this.description = description;
            this.displayOrder = displayOrder;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNameEn() { return nameEn; }
        public void setNameEn(String nameEn) { this.nameEn = nameEn; }
        public String getNameTa() { return nameTa; }
        public void setNameTa(String nameTa) { this.nameTa = nameTa; }
        public String getNameHi() { return nameHi; }
        public void setNameHi(String nameHi) { this.nameHi = nameHi; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getOfferPct() { return offerPct; }
        public void setOfferPct(Integer offerPct) { this.offerPct = offerPct; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    }

    @Entity
    @Table(name = "bookings")
    public static class Booking {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false, length = 100)
        private String clientName;
        @Column(nullable = false, length = 20)
        private String contact;
        @Column(columnDefinition = "TEXT")
        private String serviceNames;
        @Column(nullable = false)
        private LocalDate bookingDate;
        @Column(nullable = false)
        private LocalTime bookingTime;
        @Column(columnDefinition = "TEXT")
        private String notes;
        @Column(length = 20)
        private String status = "PENDING"; // PENDING, CONFIRMED, COMPLETED, CANCELLED
        private Long stylistId;
        @Column(length = 20)
        private String paymentType;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Booking() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        public String getServiceNames() { return serviceNames; }
        public void setServiceNames(String serviceNames) { this.serviceNames = serviceNames; }
        public LocalDate getBookingDate() { return bookingDate; }
        public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
        public LocalTime getBookingTime() { return bookingTime; }
        public void setBookingTime(LocalTime bookingTime) { this.bookingTime = bookingTime; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getStylistId() { return stylistId; }
        public void setStylistId(Long stylistId) { this.stylistId = stylistId; }
        public String getPaymentType() { return paymentType; }
        public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @Entity
    @Table(name = "feedback")
    public static class Feedback {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(length = 100)
        private String clientName = "Anonymous";
        private Integer serviceRating = 5;
        private Integer shopRating = 5;
        private Integer workerRating = 5;
        private Integer timingRating = 5;
        private Integer overallRating = 5;
        @Column(columnDefinition = "TEXT")
        private String comments;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Feedback() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }
        public Integer getServiceRating() { return serviceRating; }
        public void setServiceRating(Integer serviceRating) { this.serviceRating = serviceRating; }
        public Integer getShopRating() { return shopRating; }
        public void setShopRating(Integer shopRating) { this.shopRating = shopRating; }
        public Integer getWorkerRating() { return workerRating; }
        public void setWorkerRating(Integer workerRating) { this.workerRating = workerRating; }
        public Integer getTimingRating() { return timingRating; }
        public void setTimingRating(Integer timingRating) { this.timingRating = timingRating; }
        public Integer getOverallRating() { return overallRating; }
        public void setOverallRating(Integer overallRating) { this.overallRating = overallRating; }
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @Entity
    @Table(name = "daily_log")
    public static class DailyLog {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Long stylistId;
        @Column(length = 100)
        private String serviceName;
        private Integer quantity = 1;
        @Column(length = 20)
        private String paymentType = "CASH"; // CASH, ONLINE
        @Column(precision = 10, scale = 2)
        private BigDecimal amount = BigDecimal.ZERO;
        private LocalDate logDate = LocalDate.now();
        private LocalDateTime createdAt = LocalDateTime.now();

        public DailyLog() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getStylistId() { return stylistId; }
        public void setStylistId(Long stylistId) { this.stylistId = stylistId; }
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getPaymentType() { return paymentType; }
        public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public LocalDate getLogDate() { return logDate; }
        public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @Entity
    @Table(name = "inventory")
    public static class Inventory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false, length = 100)
        private String itemName;
        @Column(length = 50)
        private String category;
        private Integer currentCount = 0;
        @Column(length = 20)
        private String unit = "pcs";
        private Integer lowThreshold = 5;
        @Column(columnDefinition = "TEXT")
        private String notes;
        private LocalDateTime updatedAt = LocalDateTime.now();

        public Inventory() {}
        public Inventory(String itemName, String category, Integer currentCount, String unit, Integer lowThreshold, String notes) {
            this.itemName = itemName;
            this.category = category;
            this.currentCount = currentCount;
            this.unit = unit;
            this.lowThreshold = lowThreshold;
            this.notes = notes;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getCurrentCount() { return currentCount; }
        public void setCurrentCount(Integer currentCount) { this.currentCount = currentCount; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public Integer getLowThreshold() { return lowThreshold; }
        public void setLowThreshold(Integer lowThreshold) { this.lowThreshold = lowThreshold; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    @Entity
    @Table(name = "advertisements")
    public static class Advertisement {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(length = 10)
        private String type = "IMAGE"; // IMAGE, VIDEO
        @Column(length = 500)
        private String filePath;
        @Column(length = 200)
        private String title;
        private Integer displayOrder = 0;
        private boolean active = true;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Advertisement() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    @Entity
    @Table(name = "events")
    public static class Event {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private LocalDate eventDate;
        @Column(nullable = false, length = 200)
        private String titleEn;
        @Column(length = 200)
        private String titleTa;
        @Column(length = 200)
        private String titleHi;
        @Column(columnDefinition = "TEXT")
        private String description;
        private boolean isRecurring = false;
        @Column(length = 50)
        private String category;

        public Event() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public LocalDate getEventDate() { return eventDate; }
        public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
        public String getTitleEn() { return titleEn; }
        public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
        public String getTitleTa() { return titleTa; }
        public void setTitleTa(String titleTa) { this.titleTa = titleTa; }
        public String getTitleHi() { return titleHi; }
        public void setTitleHi(String titleHi) { this.titleHi = titleHi; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isRecurring() { return isRecurring; }
        public void setRecurring(boolean recurring) { isRecurring = recurring; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    @Entity
    @Table(name = "achievements")
    public static class Achievement {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false, length = 200)
        private String titleEn;
        @Column(length = 200)
        private String titleTa;
        @Column(length = 200)
        private String titleHi;
        @Column(columnDefinition = "TEXT")
        private String description;
        private Integer year;
        @Column(length = 20)
        private String icon;

        public Achievement() {}
        public Achievement(String titleEn, String titleTa, String titleHi, String description, Integer year, String icon) {
            this.titleEn = titleEn;
            this.titleTa = titleTa;
            this.titleHi = titleHi;
            this.description = description;
            this.year = year;
            this.icon = icon;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitleEn() { return titleEn; }
        public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
        public String getTitleTa() { return titleTa; }
        public void setTitleTa(String titleTa) { this.titleTa = titleTa; }
        public String getTitleHi() { return titleHi; }
        public void setTitleHi(String titleHi) { this.titleHi = titleHi; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    @Entity
    @Table(name = "offers")
    public static class Offer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(length = 200)
        private String titleEn;
        @Column(length = 200)
        private String titleTa;
        @Column(length = 200)
        private String titleHi;
        @Column(columnDefinition = "TEXT")
        private String description;
        private Integer discountPct = 0;
        private LocalDate validFrom;
        private LocalDate validUntil;
        private boolean active = true;

        public Offer() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitleEn() { return titleEn; }
        public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
        public String getTitleTa() { return titleTa; }
        public void setTitleTa(String titleTa) { this.titleTa = titleTa; }
        public String getTitleHi() { return titleHi; }
        public void setTitleHi(String titleHi) { this.titleHi = titleHi; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getDiscountPct() { return discountPct; }
        public void setDiscountPct(Integer discountPct) { this.discountPct = discountPct; }
        public LocalDate getValidFrom() { return validFrom; }
        public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
        public LocalDate getValidUntil() { return validUntil; }
        public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    @Entity
    @Table(name = "payment_qr")
    public static class PaymentQr {
        @Id
        private Long id = 1L;
        @Column(length = 100)
        private String upiId = "6374402014@okbizaxis";
        @Column(length = 100)
        private String displayName = "VIJAYAN SALOON";
        @Column(length = 500)
        private String qrImagePath = "assets/uploads/qr/payment_qr.jpg";
        private LocalDateTime updatedAt = LocalDateTime.now();

        public PaymentQr() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUpiId() { return upiId; }
        public void setUpiId(String upiId) { this.upiId = upiId; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getQrImagePath() { return qrImagePath; }
        public void setQrImagePath(String qrImagePath) { this.qrImagePath = qrImagePath; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
