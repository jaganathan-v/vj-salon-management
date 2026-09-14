package com.vjsalon.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DTOs {

    // Auth DTOs
    public record LoginRequest(String stylistCode, String adminCode, String password) {}
    public record AuthResponse(String token, String name, String role, Long stylistId, String stylistCode) {}
    public record ChangePasswordRequest(String currentPassword, String newPassword) {}

    // Booking DTOs
    public record BookingRequest(String clientName, String contact, String serviceNames, String bookingDate, String bookingTime, String notes) {}
    public record BookingStatusRequest(String status) {}

    // Feedback DTOs
    public record FeedbackRequest(String clientName, Integer serviceRating, Integer shopRating, Integer workerRating, Integer timingRating, Integer overallRating, String comments) {}

    // Shop Status DTOs
    public record ShopStatusRequest(boolean isOpen, String note) {}
    public record StylistStatusRequest(String status) {}

    // Daily Log DTO
    public record DailyLogRequest(String serviceName, Integer quantity, String paymentType, BigDecimal amount) {}

    // Inventory DTOs
    public record InventoryRequest(String itemName, String category, Integer currentCount, String unit, Integer lowThreshold, String notes) {}
    public record InventoryUpdateRequest(Integer currentCount) {}

    // Admin Analytics DTO
    public record AnalyticsResponse(
        String period,
        List<String> labels,
        List<Double> revenueData,
        Map<String, Integer> serviceData,
        Map<String, Integer> stylistData,
        List<Double> ratingTrends
    ) {}

    // Service DTO
    public record ServiceRequest(String nameEn, String nameTa, String nameHi, BigDecimal price, Integer offerPct, String category, String description, Integer displayOrder) {}

    // Offer DTO
    public record OfferRequest(String titleEn, String titleTa, String titleHi, String description, Integer discountPct, String validFrom, String validUntil, boolean active) {}

    // Event DTO
    public record EventRequest(String titleEn, String titleTa, String titleHi, String description, String eventDate, boolean isRecurring, String category) {}

    // Achievement DTO
    public record AchievementRequest(String titleEn, String titleTa, String titleHi, String description, Integer year, String icon) {}

    // Payment QR & Shop Settings
    public record PaymentQrUpdateRequest(String upiId, String displayName) {}
    public record ShopSettingsRequest(String shopName, String tagline, String address, String phone, String email, Integer sinceYear, String mapsLink) {}
}
