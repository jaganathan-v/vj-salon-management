package com.vjsalon.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vjsalon.dto.DTOs.AnalyticsResponse;
import com.vjsalon.dto.DTOs.AchievementRequest;
import com.vjsalon.dto.DTOs.AdvertisementRequest;
import com.vjsalon.dto.DTOs.AuthResponse;
import com.vjsalon.dto.DTOs.BookingRequest;
import com.vjsalon.dto.DTOs.BookingStatusRequest;
import com.vjsalon.dto.DTOs.ChangePasswordRequest;
import com.vjsalon.dto.DTOs.DailyLogRequest;
import com.vjsalon.dto.DTOs.EventRequest;
import com.vjsalon.dto.DTOs.FeedbackRequest;
import com.vjsalon.dto.DTOs.HomeServiceStatusRequest;
import com.vjsalon.dto.DTOs.InventoryRequest;
import com.vjsalon.dto.DTOs.InventoryUpdateRequest;
import com.vjsalon.dto.DTOs.LoginRequest;
import com.vjsalon.dto.DTOs.OfferRequest;
import com.vjsalon.dto.DTOs.ServiceRequest;
import com.vjsalon.dto.DTOs.ShopSettingsRequest;
import com.vjsalon.dto.DTOs.SitePreferencesRequest;
import com.vjsalon.dto.DTOs.ShopStatusRequest;
import com.vjsalon.dto.DTOs.StylistStatusRequest;
import com.vjsalon.dto.DTOs.StylistBookingOption;
import com.vjsalon.model.Models.Achievement;
import com.vjsalon.model.Models.Advertisement;
import com.vjsalon.model.Models.Booking;
import com.vjsalon.model.Models.DailyLog;
import com.vjsalon.model.Models.Event;
import com.vjsalon.model.Models.Feedback;
import com.vjsalon.model.Models.Inventory;
import com.vjsalon.model.Models.Offer;
import com.vjsalon.model.Models.PaymentQr;
import com.vjsalon.model.Models.SalonService;
import com.vjsalon.model.Models.ShopSettings;
import com.vjsalon.model.Models.ShopStatus;
import com.vjsalon.model.Models.Stylist;
import com.vjsalon.repository.AchievementRepository;
import com.vjsalon.repository.AdvertisementRepository;
import com.vjsalon.repository.EventRepository;
import com.vjsalon.repository.OfferRepository;
import com.vjsalon.repository.PaymentQrRepository;
import com.vjsalon.repository.SalonServiceRepository;
import com.vjsalon.repository.ShopSettingsRepository;
import com.vjsalon.repository.StylistRepository;
import com.vjsalon.service.Services.AdminAnalyticsService;
import com.vjsalon.service.Services.AuthService;
import com.vjsalon.service.Services.BookingService;
import com.vjsalon.service.Services.DailyLogService;
import com.vjsalon.service.Services.FeedbackService;
import com.vjsalon.service.Services.FileStorageService;
import com.vjsalon.service.Services.InventoryService;
import com.vjsalon.service.Services.ShopService;

public class Controllers {

    // ==========================================
    // 1. AUTH CONTROLLER
    // ==========================================
    @RestController
    @RequestMapping("/api/auth")
    public static class AuthController {
        private final AuthService authService;

        public AuthController(AuthService authService) {
            this.authService = authService;
        }

        @PostMapping("/stylist")
        public ResponseEntity<AuthResponse> loginStylist(@RequestBody LoginRequest req) {
            return ResponseEntity.ok(authService.loginStylist(req));
        }

        @PostMapping("/admin")
        public ResponseEntity<AuthResponse> loginAdmin(@RequestBody LoginRequest req) {
            return ResponseEntity.ok(authService.loginAdmin(req));
        }

        @PatchMapping("/admin/password")
        public ResponseEntity<Map<String, String>> changeAdminPassword(@RequestBody ChangePasswordRequest req) {
            authService.changeAdminPassword(req.currentPassword(), req.newPassword());
            return ResponseEntity.ok(Map.of("message", "Admin password changed successfully"));
        }
    }

    // ==========================================
    // 2. PUBLIC / CLIENT SHOP CONTROLLER
    // ==========================================
    @RestController
    @RequestMapping("/api")
    public static class ShopController {
        private final ShopService shopService;

        public ShopController(ShopService shopService) {
            this.shopService = shopService;
        }

        @GetMapping("/shop/info")
        public ResponseEntity<ShopSettings> getShopInfo() {
            return ResponseEntity.ok(shopService.getShopInfo());
        }

        @GetMapping("/site-preferences")
        public ResponseEntity<Map<String, String>> getSitePreferences() {
            ShopSettings settings = shopService.getShopInfo();
            return ResponseEntity.ok(Map.of(
                    "theme", settings.getSiteTheme() == null ? "dark" : settings.getSiteTheme(),
                    "language", settings.getSiteLanguage() == null ? "en" : settings.getSiteLanguage()));
        }

        @GetMapping("/shop/status")
        public ResponseEntity<ShopStatus> getShopStatus() {
            return ResponseEntity.ok(shopService.getShopStatus());
        }

        @PatchMapping("/shop/status")
        public ResponseEntity<ShopStatus> updateShopStatus(@RequestBody ShopStatusRequest req) {
            return ResponseEntity.ok(shopService.updateShopStatus(req.isOpen(), req.note()));
        }

        @GetMapping("/services")
        public ResponseEntity<List<SalonService>> getServices() {
            return ResponseEntity.ok(shopService.getAllActiveServices());
        }

        @GetMapping("/stylists/status")
        public ResponseEntity<List<Stylist>> getStylistsStatus() {
            return ResponseEntity.ok(shopService.getStylistsStatus());
        }

        @GetMapping("/stylists/booking-options")
        public ResponseEntity<List<StylistBookingOption>> getStylistBookingOptions() {
            return ResponseEntity.ok(shopService.getBookingStylistOptions());
        }

        @PatchMapping("/stylist/me/status")
        public ResponseEntity<Stylist> updateMyStatus(@RequestBody StylistStatusRequest req, Authentication auth) {
            String stylistCode = auth.getName();
            return ResponseEntity.ok(shopService.updateStylistStatus(stylistCode, req.status()));
        }

        @PatchMapping("/stylist/me/home-service-status")
        public ResponseEntity<Stylist> updateMyHomeServiceStatus(@RequestBody HomeServiceStatusRequest req, Authentication auth) {
            String stylistCode = auth.getName();
            return ResponseEntity.ok(shopService.updateHomeServiceStatus(stylistCode, req.homeServiceStatus()));
        }

        @GetMapping("/offers")
        public ResponseEntity<List<Offer>> getOffers() {
            return ResponseEntity.ok(shopService.getActiveOffers());
        }

        @GetMapping("/events/today")
        public ResponseEntity<List<Event>> getTodayEvents() {
            return ResponseEntity.ok(shopService.getTodayEvents());
        }

        @GetMapping("/achievements")
        public ResponseEntity<List<Achievement>> getAchievements() {
            return ResponseEntity.ok(shopService.getAllAchievements());
        }

        @GetMapping("/advertisements")
        public ResponseEntity<List<Advertisement>> getAdvertisements() {
            return ResponseEntity.ok(shopService.getActiveAdvertisements());
        }

        @GetMapping("/payment/qr")
        public ResponseEntity<PaymentQr> getPaymentQr() {
            return ResponseEntity.ok(shopService.getPaymentQr());
        }
    }

    // ==========================================
    // 3. BOOKINGS CONTROLLER
    // ==========================================
    @RestController
    @RequestMapping("/api/bookings")
    public static class BookingController {
        private final BookingService bookingService;

        public BookingController(BookingService bookingService) {
            this.bookingService = bookingService;
        }

        @PostMapping
        public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest req) {
            return ResponseEntity.ok(bookingService.createBooking(req));
        }

        @GetMapping
        public ResponseEntity<List<Booking>> getBookings() {
            return ResponseEntity.ok(bookingService.getAllBookings());
        }

        @PatchMapping("/{id}/status")
        public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long id, @RequestBody BookingStatusRequest req) {
            return ResponseEntity.ok(bookingService.updateBookingStatus(id, req.status()));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> deleteBooking(@PathVariable Long id) {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking deleted"));
        }
    }

    // ==========================================
    // 4. FEEDBACK CONTROLLER
    // ==========================================
    @RestController
    @RequestMapping("/api/feedback")
    public static class FeedbackController {
        private final FeedbackService feedbackService;

        public FeedbackController(FeedbackService feedbackService) {
            this.feedbackService = feedbackService;
        }

        @PostMapping
        public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackRequest req) {
            return ResponseEntity.ok(feedbackService.submitFeedback(req));
        }

        @GetMapping
        public ResponseEntity<List<Feedback>> getAllFeedback() {
            return ResponseEntity.ok(feedbackService.getAllFeedback());
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> deleteFeedback(@PathVariable Long id) {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok(Map.of("message", "Feedback deleted"));
        }
    }

    // ==========================================
    // 5. DAILY LOG & INVENTORY CONTROLLER
    // ==========================================
    @RestController
    @RequestMapping("/api")
    public static class OperationsController {
        private final DailyLogService dailyLogService;
        private final InventoryService inventoryService;

        public OperationsController(DailyLogService dailyLogService, InventoryService inventoryService) {
            this.dailyLogService = dailyLogService;
            this.inventoryService = inventoryService;
        }

        @PostMapping("/daily-log")
        public ResponseEntity<DailyLog> addDailyLog(@RequestBody DailyLogRequest req, Authentication auth) {
            return ResponseEntity.ok(dailyLogService.addLog(req, auth.getName()));
        }

        @GetMapping("/daily-log")
        public ResponseEntity<List<DailyLog>> getDailyLogs() {
            return ResponseEntity.ok(dailyLogService.getTodayLogs());
        }

        @GetMapping("/inventory")
        public ResponseEntity<List<Inventory>> getInventory() {
            return ResponseEntity.ok(inventoryService.getAllInventory());
        }

        @PostMapping("/inventory")
        public ResponseEntity<Inventory> addInventory(@RequestBody InventoryRequest req) {
            return ResponseEntity.ok(inventoryService.addItem(req));
        }

        @PatchMapping("/inventory/{id}")
        public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody InventoryUpdateRequest req) {
            return ResponseEntity.ok(inventoryService.updateItemCount(id, req.currentCount()));
        }

        @DeleteMapping("/inventory/{id}")
        public ResponseEntity<Map<String, String>> deleteInventory(@PathVariable Long id) {
            inventoryService.deleteItem(id);
            return ResponseEntity.ok(Map.of("message", "Inventory item removed"));
        }
    }

    // ==========================================
    // 6. ADMIN MASTER CRUD & ANALYTICS CONTROLLER
    // ==========================================
    @RestController
    @RequestMapping("/api/admin")
    public static class AdminController {
        private final AdminAnalyticsService analyticsService;
        private final SalonServiceRepository serviceRepo;
        private final StylistRepository stylistRepo;
        private final OfferRepository offerRepo;
        private final EventRepository eventRepo;
        private final AchievementRepository achievementRepo;
        private final AdvertisementRepository adRepo;
        private final PaymentQrRepository paymentQrRepo;
        private final ShopSettingsRepository settingsRepo;
        private final FileStorageService fileStorage;
        private final PasswordEncoder passwordEncoder;
        private final AuthService authService;

        public AdminController(AdminAnalyticsService analyticsService,
                               SalonServiceRepository serviceRepo,
                               StylistRepository stylistRepo,
                               OfferRepository offerRepo,
                               EventRepository eventRepo,
                               AchievementRepository achievementRepo,
                               AdvertisementRepository adRepo,
                               PaymentQrRepository paymentQrRepo,
                               ShopSettingsRepository settingsRepo,
                               FileStorageService fileStorage,
                               PasswordEncoder passwordEncoder,
                               AuthService authService) {
            this.analyticsService = analyticsService;
            this.serviceRepo = serviceRepo;
            this.stylistRepo = stylistRepo;
            this.offerRepo = offerRepo;
            this.eventRepo = eventRepo;
            this.achievementRepo = achievementRepo;
            this.adRepo = adRepo;
            this.paymentQrRepo = paymentQrRepo;
            this.settingsRepo = settingsRepo;
            this.fileStorage = fileStorage;
            this.passwordEncoder = passwordEncoder;
            this.authService = authService;
        }

        @GetMapping("/analytics")
        public ResponseEntity<AnalyticsResponse> getAnalytics(@RequestParam(defaultValue = "daily") String period) {
            return ResponseEntity.ok(analyticsService.getAnalytics(period));
        }

        // Services CRUD
        @GetMapping("/services")
        public ResponseEntity<List<SalonService>> getAllServices() {
            return ResponseEntity.ok(serviceRepo.findAll());
        }

        @PostMapping("/services")
        public ResponseEntity<SalonService> createService(@RequestBody ServiceRequest req) {
            SalonService s = new SalonService(req.nameEn(), req.nameTa(), req.nameHi(), req.price(), req.offerPct(), req.category(), req.description(), req.displayOrder());
            return ResponseEntity.ok(serviceRepo.save(s));
        }

        @PutMapping("/services/{id}")
        public ResponseEntity<SalonService> updateService(@PathVariable Long id, @RequestBody ServiceRequest req) {
            SalonService s = serviceRepo.findById(id).orElseThrow();
            s.setNameEn(req.nameEn());
            s.setNameTa(req.nameTa());
            s.setNameHi(req.nameHi());
            s.setPrice(req.price());
            s.setOfferPct(req.offerPct());
            s.setCategory(req.category());
            s.setDescription(req.description());
            return ResponseEntity.ok(serviceRepo.save(s));
        }

        @DeleteMapping("/services/{id}")
        public ResponseEntity<Void> deleteService(@PathVariable Long id) {
            serviceRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }

        // Stylists CRUD
        @GetMapping("/stylists")
        public ResponseEntity<List<Stylist>> getAllStylists() {
            return ResponseEntity.ok(stylistRepo.findAll());
        }

        @PostMapping("/stylists")
        public ResponseEntity<Stylist> createStylist(@RequestBody Map<String, String> req) {
            Stylist s = new Stylist();
            s.setName(req.get("name"));
            s.setStylistCode(req.get("stylistCode"));
            s.setPasswordHash(passwordEncoder.encode(req.get("password")));
            s.setStatus("FREE");
            s.setActive(true);
            return ResponseEntity.ok(stylistRepo.save(s));
        }

        @PatchMapping("/stylists/{id}/reset-password")
        public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> req) {
            authService.resetStylistPassword(id, req.get("newPassword"));
            return ResponseEntity.ok(Map.of("message", "Password updated"));
        }

        @DeleteMapping("/stylists/{id}")
        public ResponseEntity<Void> deactivateStylist(@PathVariable Long id) {
            Stylist s = stylistRepo.findById(id).orElseThrow();
            s.setActive(false);
            stylistRepo.save(s);
            return ResponseEntity.ok().build();
        }

        // Offers CRUD
        @GetMapping("/events")
        public ResponseEntity<List<Event>> getAllEvents() {
            return ResponseEntity.ok(eventRepo.findAll());
        }

        @PostMapping("/events")
        public ResponseEntity<Event> createEvent(@RequestBody EventRequest req) {
            Event event = new Event();
            event.setTitleEn(req.titleEn());
            event.setTitleTa(req.titleTa());
            event.setTitleHi(req.titleHi());
            event.setDescription(req.description());
            event.setEventDate(LocalDate.parse(req.eventDate()));
            event.setRecurring(req.isRecurring());
            event.setCategory(req.category());
            return ResponseEntity.ok(eventRepo.save(event));
        }

        @DeleteMapping("/events/{id}")
        public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
            eventRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }

        // Offers CRUD
        @GetMapping("/offers")
        public ResponseEntity<List<Offer>> getAllOffers() {
            return ResponseEntity.ok(offerRepo.findAll());
        }

        @PostMapping("/offers")
        public ResponseEntity<Offer> createOffer(@RequestBody OfferRequest req) {
            Offer o = new Offer();
            o.setTitleEn(req.titleEn());
            o.setTitleTa(req.titleTa());
            o.setTitleHi(req.titleHi());
            o.setDescription(req.description());
            o.setDiscountPct(req.discountPct());
            o.setValidUntil(LocalDate.parse(req.validUntil()));
            o.setActive(true);
            return ResponseEntity.ok(offerRepo.save(o));
        }

        @DeleteMapping("/offers/{id}")
        public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
            offerRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }

        // Achievements CRUD
        @GetMapping("/achievements")
        public ResponseEntity<List<Achievement>> getAllAchievements() {
            return ResponseEntity.ok(achievementRepo.findAll());
        }

        @PostMapping("/achievements")
        public ResponseEntity<Achievement> createAchievement(@RequestBody AchievementRequest req) {
            Achievement achievement = new Achievement(req.titleEn(), req.titleTa(), req.titleHi(), req.description(), req.year(), req.icon());
            return ResponseEntity.ok(achievementRepo.save(achievement));
        }

        @DeleteMapping("/achievements/{id}")
        public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
            achievementRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }

        // Media & Payment QR
        @PostMapping("/ads/upload")
        public ResponseEntity<Map<String, String>> uploadAdFile(@RequestParam("file") MultipartFile file) {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Select an image or video to upload"));
            }
            return ResponseEntity.ok(Map.of("path", fileStorage.saveFile(file, "ads")));
        }

        @PostMapping("/ads")
        public ResponseEntity<Advertisement> createAd(@RequestBody AdvertisementRequest req) {
            Advertisement ad = new Advertisement();
            ad.setTitle(req.title());
            ad.setType(req.type());
            ad.setFilePath(req.filePath());
            ad.setActive(true);
            return ResponseEntity.ok(adRepo.save(ad));
        }

        @PostMapping("/advertisements")
        public ResponseEntity<Advertisement> uploadAd(
                @RequestParam("file") MultipartFile file,
                @RequestParam("title") String title,
                @RequestParam("type") String type) {
            String path = fileStorage.saveFile(file, "ads");
            Advertisement ad = new Advertisement();
            ad.setTitle(title);
            ad.setType(type);
            ad.setFilePath(path);
            ad.setActive(true);
            return ResponseEntity.ok(adRepo.save(ad));
        }

        @PutMapping("/payment-qr")
        public ResponseEntity<PaymentQr> updatePaymentQr(
                @RequestParam("upiId") String upiId,
                @RequestParam("displayName") String displayName,
                @RequestParam(value = "file", required = false) MultipartFile file) {
            PaymentQr qr = paymentQrRepo.findById(1L).orElseGet(PaymentQr::new);
            qr.setUpiId(upiId);
            qr.setDisplayName(displayName);
            if (file != null && !file.isEmpty()) {
                String path = fileStorage.saveFile(file, "qr");
                qr.setQrImagePath(path);
            }
            return ResponseEntity.ok(paymentQrRepo.save(qr));
        }

        @PutMapping("/shop-settings")
        public ResponseEntity<ShopSettings> updateShopSettings(@RequestBody ShopSettingsRequest req) {
            ShopSettings s = settingsRepo.findById(1L).orElseGet(ShopSettings::new);
            s.setShopName(req.shopName());
            s.setTagline(req.tagline());
            s.setAddress(req.address());
            s.setPhone(req.phone());
            s.setSinceYear(req.sinceYear());
            return ResponseEntity.ok(settingsRepo.save(s));
        }

        @PutMapping("/site-preferences")
        public ResponseEntity<Map<String, String>> updateSitePreferences(@RequestBody SitePreferencesRequest req) {
            ShopSettings settings = settingsRepo.findById(1L).orElseGet(ShopSettings::new);
            if (req.theme() != null) {
                if (!List.of("dark", "light", "sepia").contains(req.theme())) {
                    return ResponseEntity.badRequest().build();
                }
                settings.setSiteTheme(req.theme());
            }
            if (req.language() != null) {
                if (!List.of("en", "ta", "hi").contains(req.language())) {
                    return ResponseEntity.badRequest().build();
                }
                settings.setSiteLanguage(req.language());
            }
            settingsRepo.save(settings);
            return ResponseEntity.ok(Map.of("theme", settings.getSiteTheme(), "language", settings.getSiteLanguage()));
        }
    }
}
