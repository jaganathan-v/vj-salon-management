package com.vjsalon.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vjsalon.config.JwtUtil;
import com.vjsalon.dto.DTOs.AnalyticsResponse;
import com.vjsalon.dto.DTOs.AuthResponse;
import com.vjsalon.dto.DTOs.BookingRequest;
import com.vjsalon.dto.DTOs.DailyLogRequest;
import com.vjsalon.dto.DTOs.FeedbackRequest;
import com.vjsalon.dto.DTOs.InventoryRequest;
import com.vjsalon.dto.DTOs.LoginRequest;
import com.vjsalon.model.Models.Achievement;
import com.vjsalon.model.Models.AdminConfig;
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
import com.vjsalon.repository.AdminConfigRepository;
import com.vjsalon.repository.AdvertisementRepository;
import com.vjsalon.repository.BookingRepository;
import com.vjsalon.repository.DailyLogRepository;
import com.vjsalon.repository.EventRepository;
import com.vjsalon.repository.FeedbackRepository;
import com.vjsalon.repository.InventoryRepository;
import com.vjsalon.repository.OfferRepository;
import com.vjsalon.repository.PaymentQrRepository;
import com.vjsalon.repository.SalonServiceRepository;
import com.vjsalon.repository.ShopSettingsRepository;
import com.vjsalon.repository.ShopStatusRepository;
import com.vjsalon.repository.StylistRepository;

public class Services {

    @Service
    public static class AuthService {
        private final StylistRepository stylistRepo;
        private final AdminConfigRepository adminRepo;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

        public AuthService(StylistRepository stylistRepo, AdminConfigRepository adminRepo,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
            this.stylistRepo = stylistRepo;
            this.adminRepo = adminRepo;
            this.passwordEncoder = passwordEncoder;
            this.jwtUtil = jwtUtil;
        }

        public AuthResponse loginStylist(LoginRequest req) {
            Stylist stylist = stylistRepo.findByStylistCode(req.stylistCode())
                    .orElseThrow(() -> new RuntimeException("Stylist code not found: " + req.stylistCode()));

            if (!stylist.isActive()) {
                throw new RuntimeException("Stylist account is deactivated");
            }

            if (!passwordEncoder.matches(req.password(), stylist.getPasswordHash())) {
                throw new RuntimeException("Invalid stylist password");
            }

            String token = jwtUtil.generateToken(stylist.getStylistCode(), "STYLIST");
            return new AuthResponse(token, stylist.getName(), "STYLIST", stylist.getId(), stylist.getStylistCode());
        }

        public AuthResponse loginAdmin(LoginRequest req) {
            AdminConfig admin = adminRepo.findByAdminCode(req.adminCode())
                    .orElseThrow(() -> new RuntimeException("Admin code not found: " + req.adminCode()));

            if (!passwordEncoder.matches(req.password(), admin.getPasswordHash())) {
                throw new RuntimeException("Invalid admin password");
            }

            String token = jwtUtil.generateToken(admin.getAdminCode(), "ADMIN");
            return new AuthResponse(token, "Master Admin", "ADMIN", null, admin.getAdminCode());
        }

        @Transactional
        public void changeAdminPassword(String currentPassword, String newPassword) {
            AdminConfig admin = adminRepo.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Admin config not found"));

            if (!passwordEncoder.matches(currentPassword, admin.getPasswordHash())) {
                throw new RuntimeException("Current password does not match");
            }

            admin.setPasswordHash(passwordEncoder.encode(newPassword));
            adminRepo.save(admin);
        }

        @Transactional
        public void resetStylistPassword(Long stylistId, String newPassword) {
            Stylist stylist = stylistRepo.findById(stylistId)
                    .orElseThrow(() -> new RuntimeException("Stylist not found with ID: " + stylistId));
            stylist.setPasswordHash(passwordEncoder.encode(newPassword));
            stylistRepo.save(stylist);
        }
    }

    @Service
    public static class ShopService {
        private final ShopSettingsRepository settingsRepo;
        private final ShopStatusRepository statusRepo;
        private final SalonServiceRepository serviceRepo;
        private final StylistRepository stylistRepo;
        private final OfferRepository offerRepo;
        private final EventRepository eventRepo;
        private final AchievementRepository achievementRepo;
        private final AdvertisementRepository adRepo;
        private final PaymentQrRepository paymentQrRepo;

        public ShopService(ShopSettingsRepository settingsRepo, ShopStatusRepository statusRepo,
                           SalonServiceRepository serviceRepo, StylistRepository stylistRepo,
                           OfferRepository offerRepo, EventRepository eventRepo,
                           AchievementRepository achievementRepo, AdvertisementRepository adRepo,
                           PaymentQrRepository paymentQrRepo) {
            this.settingsRepo = settingsRepo;
            this.statusRepo = statusRepo;
            this.serviceRepo = serviceRepo;
            this.stylistRepo = stylistRepo;
            this.offerRepo = offerRepo;
            this.eventRepo = eventRepo;
            this.achievementRepo = achievementRepo;
            this.adRepo = adRepo;
            this.paymentQrRepo = paymentQrRepo;
        }

        public ShopSettings getShopInfo() {
            return settingsRepo.findById(1L).orElseGet(ShopSettings::new);
        }

        public ShopStatus getShopStatus() {
            return statusRepo.findById(1L).orElseGet(ShopStatus::new);
        }

        @Transactional
        public ShopStatus updateShopStatus(boolean isOpen, String note) {
            ShopStatus status = statusRepo.findById(1L).orElseGet(ShopStatus::new);
            status.setOpen(isOpen);
            status.setNote(note);
            if (isOpen) {
                status.setOpenedAt(LocalDateTime.now());
            } else {
                status.setClosedAt(LocalDateTime.now());
            }
            return statusRepo.save(status);
        }

        public List<SalonService> getAllActiveServices() {
            return serviceRepo.findByActiveTrueOrderByDisplayOrderAsc();
        }

        public List<Stylist> getStylistsStatus() {
            return stylistRepo.findByActiveTrue();
        }

        @Transactional
        public Stylist updateStylistStatus(String stylistCode, String status) {
            Stylist s = stylistRepo.findByStylistCode(stylistCode)
                    .orElseThrow(() -> new RuntimeException("Stylist not found: " + stylistCode));
            s.setStatus(status);
            return stylistRepo.save(s);
        }

        public List<Offer> getActiveOffers() {
            return offerRepo.findByActiveTrueAndValidUntilGreaterThanEqual(LocalDate.now());
        }

        public List<Event> getTodayEvents() {
            return eventRepo.findByEventDate(LocalDate.now());
        }

        public List<Achievement> getAllAchievements() {
            return achievementRepo.findAllByOrderByYearDesc();
        }

        public List<Advertisement> getActiveAdvertisements() {
            return adRepo.findByActiveTrueOrderByDisplayOrderAsc();
        }

        public PaymentQr getPaymentQr() {
            return paymentQrRepo.findById(1L).orElseGet(PaymentQr::new);
        }
    }

    @Service
    public static class BookingService {
        private final BookingRepository bookingRepo;

        public BookingService(BookingRepository bookingRepo) {
            this.bookingRepo = bookingRepo;
        }

        @Transactional
        public Booking createBooking(BookingRequest req) {
            Booking b = new Booking();
            b.setClientName(req.clientName());
            b.setContact(req.contact());
            b.setServiceNames(req.serviceNames());
            b.setBookingDate(LocalDate.parse(req.bookingDate()));
            b.setBookingTime(LocalTime.parse(req.bookingTime()));
            b.setNotes(req.notes());
            b.setStatus("PENDING");
            b.setCreatedAt(LocalDateTime.now());
            return bookingRepo.save(b);
        }

        public List<Booking> getAllBookings() {
            return bookingRepo.findAllByOrderByCreatedAtDesc();
        }

        @Transactional
        public Booking updateBookingStatus(Long id, String status) {
            Booking b = bookingRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
            b.setStatus(status);
            return bookingRepo.save(b);
        }

        @Transactional
        public void deleteBooking(Long id) {
            bookingRepo.deleteById(id);
        }
    }

    @Service
    public static class FeedbackService {
        private final FeedbackRepository feedbackRepo;

        public FeedbackService(FeedbackRepository feedbackRepo) {
            this.feedbackRepo = feedbackRepo;
        }

        @Transactional
        public Feedback submitFeedback(FeedbackRequest req) {
            Feedback f = new Feedback();
            f.setClientName(req.clientName() != null && !req.clientName().isBlank() ? req.clientName() : "Anonymous");
            f.setServiceRating(req.serviceRating() != null ? req.serviceRating() : 5);
            f.setShopRating(req.shopRating() != null ? req.shopRating() : 5);
            f.setWorkerRating(req.workerRating() != null ? req.workerRating() : 5);
            f.setTimingRating(req.timingRating() != null ? req.timingRating() : 5);
            f.setOverallRating(req.overallRating() != null ? req.overallRating() : 5);
            f.setComments(req.comments());
            f.setCreatedAt(LocalDateTime.now());
            return feedbackRepo.save(f);
        }

        public List<Feedback> getAllFeedback() {
            return feedbackRepo.findAllByOrderByCreatedAtDesc();
        }

        @Transactional
        public void deleteFeedback(Long id) {
            feedbackRepo.deleteById(id);
        }
    }

    @Service
    public static class DailyLogService {
        private final DailyLogRepository dailyLogRepo;

        public DailyLogService(DailyLogRepository dailyLogRepo) {
            this.dailyLogRepo = dailyLogRepo;
        }

        @Transactional
        public DailyLog addLog(DailyLogRequest req) {
            DailyLog log = new DailyLog();
            log.setServiceName(req.serviceName());
            log.setQuantity(req.quantity() != null ? req.quantity() : 1);
            log.setAmount(req.amount());
            log.setPaymentType(req.paymentType() != null ? req.paymentType() : "CASH");
            log.setLogDate(LocalDate.now());
            log.setCreatedAt(LocalDateTime.now());
            return dailyLogRepo.save(log);
        }

        public List<DailyLog> getTodayLogs() {
            return dailyLogRepo.findByLogDateOrderByCreatedAtDesc(LocalDate.now());
        }
    }

    @Service
    public static class InventoryService {
        private final InventoryRepository inventoryRepo;

        public InventoryService(InventoryRepository inventoryRepo) {
            this.inventoryRepo = inventoryRepo;
        }

        public List<Inventory> getAllInventory() {
            return inventoryRepo.findAllByOrderByItemNameAsc();
        }

        @Transactional
        public Inventory addItem(InventoryRequest req) {
            Inventory item = new Inventory(
                req.itemName(), req.category(), req.currentCount(), req.unit(), req.lowThreshold(), req.notes()
            );
            return inventoryRepo.save(item);
        }

        @Transactional
        public Inventory updateItemCount(Long id, Integer newCount) {
            Inventory item = inventoryRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + id));
            item.setCurrentCount(newCount);
            item.setUpdatedAt(LocalDateTime.now());
            return inventoryRepo.save(item);
        }

        @Transactional
        public void deleteItem(Long id) {
            inventoryRepo.deleteById(id);
        }
    }

    @Service
    public static class AdminAnalyticsService {
        private final DailyLogRepository dailyLogRepo;

        public AdminAnalyticsService(DailyLogRepository dailyLogRepo) {
            this.dailyLogRepo = dailyLogRepo;
        }

        public AnalyticsResponse getAnalytics(String period) {
            if ("weekly".equalsIgnoreCase(period)) {
                return new AnalyticsResponse(
                    "weekly",
                    List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                    List.of(3800.0, 4200.0, 3900.0, 5100.0, 6800.0, 9400.0, 8100.0),
                    Map.of("Haircut", 120, "Beard Trim", 85, "Hair Colour", 35, "Facial", 22, "Shave", 40),
                    Map.of("Vijayan", 65, "Kumar", 52),
                    List.of(4.7, 4.8, 4.7, 4.9, 4.8, 4.9, 4.8)
                );
            } else if ("monthly".equalsIgnoreCase(period)) {
                return new AnalyticsResponse(
                    "monthly",
                    List.of("Week 1", "Week 2", "Week 3", "Week 4"),
                    List.of(28000.0, 31500.0, 29800.0, 34200.0),
                    Map.of("Haircut", 480, "Beard Trim", 320, "Hair Colour", 140, "Facial", 88, "Shave", 160),
                    Map.of("Vijayan", 280, "Kumar", 230),
                    List.of(4.6, 4.8, 4.7, 4.9)
                );
            } else if ("yearly".equalsIgnoreCase(period)) {
                return new AnalyticsResponse(
                    "yearly",
                    List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                    List.of(95000.0, 110000.0, 105000.0, 118000.0, 125000.0, 112000.0, 130000.0, 118500.0, 0.0, 0.0, 0.0, 0.0),
                    Map.of("Haircut", 5400, "Beard Trim", 3800, "Hair Colour", 1600, "Facial", 950, "Shave", 1900),
                    Map.of("Vijayan", 2800, "Kumar", 2400),
                    List.of(4.6, 4.7, 4.8, 4.8, 4.9, 4.8, 4.9, 4.8, 4.8, 4.8, 4.8, 4.8)
                );
            } else {
                // Default: Daily
                return new AnalyticsResponse(
                    "daily",
                    List.of("9 AM", "11 AM", "1 PM", "3 PM", "5 PM", "7 PM"),
                    List.of(450.0, 920.0, 600.0, 1100.0, 1400.0, 850.0),
                    Map.of("Haircut", 18, "Beard Trim", 12, "Hair Colour", 4, "Facial", 2, "Shave", 6),
                    Map.of("Vijayan", 12, "Kumar", 9),
                    List.of(4.8, 4.9, 4.7, 4.8, 4.9, 4.8)
                );
            }
        }
    }

    @Service
    public static class FileStorageService {
        private final Path uploadDir = Paths.get("uploads");

        public FileStorageService() {
            try {
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
            } catch (IOException e) {
                System.err.println("Could not initialize upload folder: " + e.getMessage());
            }
        }

        public String saveFile(MultipartFile file, String subfolder) {
            try {
                Path targetDir = uploadDir.resolve(subfolder);
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path targetPath = targetDir.resolve(filename);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                return "uploads/" + subfolder + "/" + filename;
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
    }
}
