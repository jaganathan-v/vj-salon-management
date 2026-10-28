package com.vjsalon.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vjsalon.config.JwtUtil;
import com.vjsalon.dto.DTOs.AnalyticsResponse;
import com.vjsalon.dto.DTOs.AuthResponse;
import com.vjsalon.dto.DTOs.BookingRequest;
import com.vjsalon.dto.DTOs.DailyLogRequest;
import com.vjsalon.dto.DTOs.StylistBookingOption;
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
import com.vjsalon.model.Models.HomeServiceStatus;
import com.vjsalon.model.Models.Offer;
import com.vjsalon.model.Models.PaymentQr;
import com.vjsalon.model.Models.SalonService;
import com.vjsalon.model.Models.ShopSettings;
import com.vjsalon.model.Models.ShopStatus;
import com.vjsalon.model.Models.ServiceLocation;
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
        private final BookingRepository bookingRepo;
        private final FeedbackRepository feedbackRepo;
        private final DailyLogRepository dailyLogRepo;

        public ShopService(ShopSettingsRepository settingsRepo, ShopStatusRepository statusRepo,
                           SalonServiceRepository serviceRepo, StylistRepository stylistRepo,
                           OfferRepository offerRepo, EventRepository eventRepo,
                           AchievementRepository achievementRepo, AdvertisementRepository adRepo,
                           PaymentQrRepository paymentQrRepo, BookingRepository bookingRepo,
                           FeedbackRepository feedbackRepo, DailyLogRepository dailyLogRepo) {
            this.settingsRepo = settingsRepo;
            this.statusRepo = statusRepo;
            this.serviceRepo = serviceRepo;
            this.stylistRepo = stylistRepo;
            this.offerRepo = offerRepo;
            this.eventRepo = eventRepo;
            this.achievementRepo = achievementRepo;
            this.adRepo = adRepo;
            this.paymentQrRepo = paymentQrRepo;
            this.bookingRepo = bookingRepo;
            this.feedbackRepo = feedbackRepo;
            this.dailyLogRepo = dailyLogRepo;
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
            releaseExpiredStylistStatuses();
            return stylistRepo.findByActiveTrue();
        }

        @Scheduled(fixedDelay = 30000)
        @Transactional
        public void releaseExpiredStylistStatuses() {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            for (Stylist stylist : stylistRepo.findAll()) {
                if (!"FREE".equals(stylist.getStatus()) && stylist.getAvailableAt() != null
                        && !stylist.getAvailableAt().isAfter(now)) {
                    stylist.setStatus("FREE");
                    stylist.setAvailableAt(null);
                    stylistRepo.save(stylist);
                }
            }
        }

        public List<StylistBookingOption> getBookingStylistOptions() {
            releaseExpiredStylistStatuses();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            List<Booking> bookings = bookingRepo.findAll();
            List<Feedback> feedback = feedbackRepo.findAll();
            List<DailyLog> workLogs = dailyLogRepo.findAll();
            Map<Long, Booking> bookingsById = bookings.stream()
                    .filter(b -> b.getId() != null)
                    .collect(Collectors.toMap(Booking::getId, b -> b, (a, b) -> a));

            return stylistRepo.findByActiveTrue().stream().map(stylist -> {
                List<Booking> assigned = bookings.stream()
                        .filter(b -> stylist.getId().equals(b.getStylistId()))
                        .toList();
                double ratingTotal = 0;
                long ratingCount = 0;
                for (Feedback item : feedback) {
                    Booking ratedBooking = item.getBookingId() == null ? null : bookingsById.get(item.getBookingId());
                    if (ratedBooking != null && stylist.getId().equals(ratedBooking.getStylistId())
                            && item.getWorkerRating() != null) {
                        ratingTotal += item.getWorkerRating();
                        ratingCount++;
                    }
                }

                Map<String, Long> serviceCounts = new HashMap<>();
                for (DailyLog log : workLogs) {
                    if (stylist.getId().equals(log.getStylistId()) && log.getServiceName() != null
                            && !log.getServiceName().isBlank()) {
                        serviceCounts.merge(log.getServiceName().trim(), 1L, Long::sum);
                    }
                }
                List<String> historicalSpecialties = serviceCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                                .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)))
                        .map(Map.Entry::getKey).toList();
                List<String> specialties = new ArrayList<>();
                if (stylist.getSkills() != null && !stylist.getSkills().isBlank()) {
                    for (String skill : stylist.getSkills().split(",")) {
                        String normalized = skill.trim();
                        if (!normalized.isEmpty() && !specialties.contains(normalized)) specialties.add(normalized);
                    }
                }
                for (String skill : historicalSpecialties) {
                    if (!specialties.contains(skill)) specialties.add(skill);
                }
                specialties = specialties.stream().limit(3).toList();

                String currentStatus = "FREE".equals(stylist.getStatus()) ? "FREE" : "BUSY";
                String estimatedFreeTime = null;
                if ("BUSY".equals(currentStatus)) {
                    if (stylist.getAvailableAt() != null) {
                        LocalDateTime freeAt = stylist.getAvailableAt();
                        estimatedFreeTime = freeAt.toLocalDate().equals(now.toLocalDate())
                                ? freeAt.format(DateTimeFormatter.ofPattern("h:mm a"))
                                : freeAt.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"));
                    } else {
                        Booking nextBooking = assigned.stream()
                                .filter(b -> b.getBookingDate() != null && b.getBookingTime() != null)
                                .filter(b -> !"CANCELLED".equals(b.getStatus()) && !"COMPLETED".equals(b.getStatus()))
                                .filter(b -> !b.getBookingDate().atTime(b.getBookingTime()).isBefore(now))
                                .min(Comparator.comparing(b -> b.getBookingDate().atTime(b.getBookingTime())))
                                .orElse(null);
                        if (nextBooking != null) {
                            LocalDateTime freeAt = nextBooking.getBookingDate().atTime(nextBooking.getBookingTime()).plusMinutes(60);
                            estimatedFreeTime = freeAt.toLocalDate().equals(now.toLocalDate())
                                    ? freeAt.format(DateTimeFormatter.ofPattern("h:mm a"))
                                    : freeAt.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"));
                        }
                    }
                }
                Double averageRating = ratingCount == 0 ? null : Math.round((ratingTotal / ratingCount) * 10.0) / 10.0;
                return new StylistBookingOption(stylist.getId(), stylist.getStylistCode(), stylist.getName(),
                        currentStatus, estimatedFreeTime, averageRating, ratingCount, specialties);
            }).toList();
        }

        @Transactional
        public Stylist updateStylistStatus(String stylistCode, String status, LocalDateTime availableAt) {
            Stylist s = stylistRepo.findByStylistCode(stylistCode)
                    .orElseThrow(() -> new RuntimeException("Stylist not found: " + stylistCode));
            String normalizedStatus = status == null ? "" : status.trim().toUpperCase();
            if (!List.of("FREE", "BUSY", "FOOD_BREAK").contains(normalizedStatus)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose Free, Busy, or Food Break.");
            }
            if (!"FREE".equals(normalizedStatus)
                    && (availableAt == null || !availableAt.isAfter(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Set a future time when you will be available again.");
            }
            s.setStatus(normalizedStatus);
            s.setAvailableAt("FREE".equals(normalizedStatus) ? null : availableAt);
            return stylistRepo.save(s);
        }

        @Transactional
        public Stylist updateHomeServiceStatus(String stylistCode, HomeServiceStatus status) {
            Stylist stylist = stylistRepo.findByStylistCode(stylistCode)
                    .orElseThrow(() -> new RuntimeException("Stylist not found: " + stylistCode));
            stylist.setHomeServiceStatus(status);
            return stylistRepo.save(stylist);
        }

        public List<Offer> getActiveOffers() {
            return offerRepo.findByActiveTrueAndValidUntilGreaterThanEqual(LocalDate.now());
        }

        public List<Event> getTodayEvents() {
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
            return eventRepo.findAll().stream()
                    .filter(event -> event.getEventDate() != null)
                    .filter(event -> event.getEventDate().equals(today)
                            || (event.isRecurring()
                                && event.getEventDate().getMonthValue() == today.getMonthValue()
                                && event.getEventDate().getDayOfMonth() == today.getDayOfMonth()))
                    .sorted(Comparator.comparing((Event event) -> !event.getEventDate().equals(today))
                            .thenComparing(Event::getTitleEn, String.CASE_INSENSITIVE_ORDER))
                    .toList();
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
        private final StylistRepository stylistRepo;

        public BookingService(BookingRepository bookingRepo, StylistRepository stylistRepo) {
            this.bookingRepo = bookingRepo;
            this.stylistRepo = stylistRepo;
        }

        @Transactional
        public Booking createBooking(BookingRequest req) {
            Booking b = new Booking();
            b.setClientName(req.clientName());
            b.setContact(req.contact());
            b.setServiceNames(req.serviceNames());
            b.setTotalAmount(req.totalAmount());
            b.setBookingDate(LocalDate.parse(req.bookingDate()));
            b.setBookingTime(LocalTime.parse(req.bookingTime()));
            ServiceLocation location = req.serviceLocation() == null ? ServiceLocation.AT_SHOP : req.serviceLocation();
            if (location == ServiceLocation.AT_HOME && (req.address() == null || req.address().isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is required for home service bookings");
            }
            b.setServiceLocation(location);
            b.setAddress(location == ServiceLocation.AT_HOME ? req.address().trim() : null);
            b.setNotes(req.notes());
            b.setStatus("PENDING");
            b.setCreatedAt(LocalDateTime.now());
            if (req.stylistId() != null) {
                Stylist stylist = stylistRepo.findById(req.stylistId())
                        .filter(Stylist::isActive)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected stylist is unavailable"));
                b.setStylistId(stylist.getId());
                stylist.setStatus("BUSY");
                stylist.setAvailableAt(b.getBookingDate().atTime(b.getBookingTime()).plusMinutes(60));
                stylistRepo.save(stylist);
            }
            return bookingRepo.save(b);
        }

        public List<Booking> getAllBookings() {
            return bookingRepo.findAllByOrderByCreatedAtDesc();
        }

        public List<Booking> getBookingsForStylist(String stylistCode) {
            Stylist stylist = stylistRepo.findByStylistCode(stylistCode)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Stylist account not found"));
            return bookingRepo.findByStylistIdOrderByCreatedAtDesc(stylist.getId());
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
        private final BookingRepository bookingRepo;

        public FeedbackService(FeedbackRepository feedbackRepo, BookingRepository bookingRepo) {
            this.feedbackRepo = feedbackRepo;
            this.bookingRepo = bookingRepo;
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
            if (req.bookingId() != null) {
                if (!bookingRepo.existsById(req.bookingId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found for feedback");
                }
                f.setBookingId(req.bookingId());
            }
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
        private final StylistRepository stylistRepo;

        public DailyLogService(DailyLogRepository dailyLogRepo, StylistRepository stylistRepo) {
            this.dailyLogRepo = dailyLogRepo;
            this.stylistRepo = stylistRepo;
        }

        @Transactional
        public DailyLog addLog(DailyLogRequest req, String stylistCode) {
            DailyLog log = new DailyLog();
            Stylist stylist = stylistRepo.findByStylistCode(stylistCode)
                    .orElseThrow(() -> new RuntimeException("Stylist not found: " + stylistCode));
            log.setStylistId(stylist.getId());
            log.setServiceName(req.serviceName());
            log.setServiceLocation(req.serviceLocation() == null ? ServiceLocation.AT_SHOP : req.serviceLocation());
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
            String selectedPeriod = List.of("daily", "weekly", "monthly", "yearly").stream()
                .filter(value -> value.equalsIgnoreCase(period == null ? "" : period))
                .findFirst()
                .orElse("daily");
            return new AnalyticsResponse(selectedPeriod, List.of(), List.of(), Map.of(), Map.of(), List.of());
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
                String originalName = file.getOriginalFilename() == null ? "upload" : Paths.get(file.getOriginalFilename()).getFileName().toString();
                String extension = originalName.lastIndexOf('.') >= 0 ? originalName.substring(originalName.lastIndexOf('.')).replaceAll("[^.a-zA-Z0-9]", "") : "";
                String filename = UUID.randomUUID() + extension;
                Path targetPath = targetDir.resolve(filename);
                Files.copy(file.getInputStream(), targetPath);
                return "/uploads/" + subfolder + "/" + filename;
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
    }
}
