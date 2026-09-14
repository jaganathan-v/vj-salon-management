package com.vjsalon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vjsalon.model.Models.Achievement;
import com.vjsalon.model.Models.AdminConfig;
import com.vjsalon.model.Models.Booking;
import com.vjsalon.model.Models.DailyLog;
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
import com.vjsalon.repository.BookingRepository;
import com.vjsalon.repository.DailyLogRepository;
import com.vjsalon.repository.FeedbackRepository;
import com.vjsalon.repository.InventoryRepository;
import com.vjsalon.repository.OfferRepository;
import com.vjsalon.repository.PaymentQrRepository;
import com.vjsalon.repository.SalonServiceRepository;
import com.vjsalon.repository.ShopSettingsRepository;
import com.vjsalon.repository.ShopStatusRepository;
import com.vjsalon.repository.StylistRepository;

@SpringBootApplication
public class VjSalonApplication {

    public static void main(String[] args) {
        SpringApplication.run(VjSalonApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataSeeder(
            ShopSettingsRepository shopSettingsRepo,
            ShopStatusRepository shopStatusRepo,
            AdminConfigRepository adminConfigRepo,
            StylistRepository stylistRepo,
            SalonServiceRepository serviceRepo,
            PaymentQrRepository paymentQrRepo,
            AchievementRepository achievementRepo,
            OfferRepository offerRepo,
            InventoryRepository inventoryRepo,
            BookingRepository bookingRepo,
            FeedbackRepository feedbackRepo,
            DailyLogRepository dailyLogRepo,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Shop Settings Seed
            if (shopSettingsRepo.count() == 0) {
                ShopSettings settings = new ShopSettings();
                settings.setId(1L);
                settings.setShopName("VIJAYAN SALON");
                settings.setTagline("Excellence in Every Cut");
                settings.setAddress("No.2 pillayar kovil street, Ponnammapet, Salem - 636001 (near Mariyamman kovil)");
                settings.setPhone("6374402014");
                settings.setEmail("vijayansalon@gmail.com");
                settings.setSinceYear(2010);
                settings.setMapsLink("https://maps.google.com/maps?q=Ponnammapet+Salem+636001&output=embed");
                shopSettingsRepo.save(settings);
            }

            // 2. Shop Status Seed
            if (shopStatusRepo.count() == 0) {
                ShopStatus status = new ShopStatus();
                status.setId(1L);
                status.setOpen(true);
                status.setNote("Welcome to VIJAYAN SALON! Open for walk-ins and bookings.");
                shopStatusRepo.save(status);
            }

            // 3. Admin Config Seed (Default: VJADMIN / vj@admin2024)
            if (adminConfigRepo.count() == 0) {
                AdminConfig admin = new AdminConfig();
                admin.setId(1L);
                admin.setAdminCode("VJADMIN");
                admin.setPasswordHash(passwordEncoder.encode("vj@admin2024"));
                admin.setEmail("admin@vjsalon.com");
                adminConfigRepo.save(admin);
            }

            // 4. Stylists Seed (VJS001 / stylist1, VJS002 / stylist2)
            if (stylistRepo.count() == 0) {
                Stylist s1 = new Stylist();
                s1.setStylistCode("VJS001");
                s1.setName("Vijayan");
                s1.setPasswordHash(passwordEncoder.encode("stylist1"));
                s1.setStatus("FREE");
                s1.setActive(true);

                Stylist s2 = new Stylist();
                s2.setStylistCode("VJS002");
                s2.setName("Kumar");
                s2.setPasswordHash(passwordEncoder.encode("stylist2"));
                s2.setStatus("BUSY");
                s2.setActive(true);

                stylistRepo.saveAll(List.of(s1, s2));
            }

            // 5. Services Seed (Multilingual: En, Ta, Hi)
            if (serviceRepo.count() == 0) {
                serviceRepo.saveAll(List.of(
                    new SalonService("Haircut", "முடி வெட்டு", "बाल कटाई", new BigDecimal("80.00"), 0, "HAIR", "Professional stylish haircut tailored to face shape.", 1),
                    new SalonService("Beard Trim", "தாடி கத்திரி", "दाढ़ी ट्रिम", new BigDecimal("50.00"), 0, "BEARD", "Sharp beard shape, grooming and outline trimming.", 2),
                    new SalonService("Shave", "ஷேவ்", "शेव", new BigDecimal("60.00"), 0, "BEARD", "Smooth hot-towel clean razor shave.", 3),
                    new SalonService("Hair Colour", "முடி நிறம்", "बाल रंग", new BigDecimal("300.00"), 10, "HAIR", "Natural black and rich dark brown ammonia-free hair dye.", 4),
                    new SalonService("Facial", "முகப்பூச்சு", "फेशियल", new BigDecimal("200.00"), 0, "SKIN", "Refreshing fruit scrub and skin cleansing massage.", 5),
                    new SalonService("Head Massage", "தலை மசாஜ்", "सिर मालिश", new BigDecimal("100.00"), 0, "HAIR", "Relaxing 20-minute herbal oil scalp massage.", 6),
                    new SalonService("Kids Haircut", "குழந்தை முடி வெட்டு", "बच्चों का कटाई", new BigDecimal("60.00"), 0, "HAIR", "Gentle, friendly haircut for children.", 7),
                    new SalonService("Threading", "நூல் நீக்கம்", "थ्रेडिंग", new BigDecimal("30.00"), 0, "SKIN", "Clean eyebrow and forehead precision threading.", 8)
                ));
            }

            // 6. Payment QR Seed
            if (paymentQrRepo.count() == 0) {
                PaymentQr qr = new PaymentQr();
                qr.setId(1L);
                qr.setUpiId("6374402014@okbizaxis");
                qr.setDisplayName("VIJAYAN SALOON");
                qr.setQrImagePath("assets/uploads/qr/payment_qr.jpg");
                paymentQrRepo.save(qr);
            }

            // 7. Achievements Seed
            if (achievementRepo.count() == 0) {
                achievementRepo.saveAll(List.of(
                    new Achievement("Best Salon in Salem", "சேலத்தின் சிறந்த சலூன் விருது", "सलेम का सर्वश्रेष्ठ सैलून", "Recognized by Salem Business Council for excellence in customer grooming.", 2018, "🏆"),
                    new Achievement("500+ Happy Regular Clients", "500+ திருப்திகரமான வாடிக்கையாளர்கள்", "500+ खुश नियमित ग्राहक", "Milestone of 500 loyal repeating clients across Ponnammapet.", 2020, "⭐"),
                    new Achievement("Google 4.8★ Top Rated", "கூகிள் 4.8★ உயர் மதிப்பீடு", "गूगल 4.8★ शीर्ष रेटेड", "Consistently rated 4.8+ stars for hygiene and punctuality.", 2023, "🎖")
                ));
            }

            // 8. Offers Seed
            if (offerRepo.count() == 0) {
                Offer offer = new Offer();
                offer.setTitleEn("Festival Grooming Special");
                offer.setTitleTa("திருவிழா சிறப்பு தள்ளுபடி");
                offer.setTitleHi("त्योहार विशेष छूट");
                offer.setDescription("Get 20% discount on all Hair Colour & Facial combos this season!");
                offer.setDiscountPct(20);
                offer.setValidFrom(LocalDate.now().minusDays(10));
                offer.setValidUntil(LocalDate.now().plusMonths(6));
                offer.setActive(true);
                offerRepo.save(offer);
            }

            // 9. Inventory Seed
            if (inventoryRepo.count() == 0) {
                inventoryRepo.saveAll(List.of(
                    new Inventory("Gillette Razor Blades", "Razor", 18, "pcs", 5, "Standard double-edge blades"),
                    new Inventory("Clinic Plus Shampoo", "Shampoo", 4, "bottles", 5, "Hair wash station bottles"),
                    new Inventory("Old Spice Shaving Cream", "Cream", 8, "pcs", 3, "Hot lather cream"),
                    new Inventory("Godrej Expert Hair Colour", "Colour", 6, "packs", 3, "Natural black sachets"),
                    new Inventory("Herbal Massage Oil", "Oil", 5, "bottles", 2, "Cooling scalp oil"),
                    new Inventory("Cotton Towels", "Towel", 25, "pcs", 10, "Fresh sterilized salon towels")
                ));
            }

            // 10. Sample Bookings & Feedback Seed
            if (bookingRepo.count() == 0) {
                Booking b1 = new Booking();
                b1.setClientName("Rajesh Kumar");
                b1.setContact("9876543210");
                b1.setServiceNames("Haircut, Beard Trim");
                b1.setBookingDate(LocalDate.now());
                b1.setBookingTime(LocalTime.of(10, 30));
                b1.setStatus("CONFIRMED");
                b1.setNotes("Regular trim, please keep sides short");

                Booking b2 = new Booking();
                b2.setClientName("Muthu Selvam");
                b2.setContact("8765432109");
                b2.setServiceNames("Hair Colour");
                b2.setBookingDate(LocalDate.now());
                b2.setBookingTime(LocalTime.of(12, 0));
                b2.setStatus("PENDING");
                b2.setNotes("First time hair colour");

                bookingRepo.saveAll(List.of(b1, b2));
            }

            if (feedbackRepo.count() == 0) {
                Feedback fb = new Feedback();
                fb.setClientName("Anand G.");
                fb.setServiceRating(5);
                fb.setShopRating(5);
                fb.setWorkerRating(5);
                fb.setTimingRating(4);
                fb.setOverallRating(5);
                fb.setComments("Wonderful haircut by Vijayan! Very polite and hygienic environment.");
                feedbackRepo.save(fb);
            }

            if (dailyLogRepo.count() == 0) {
                DailyLog log1 = new DailyLog();
                log1.setServiceName("Haircut");
                log1.setQuantity(2);
                log1.setAmount(new BigDecimal("160.00"));
                log1.setPaymentType("CASH");
                log1.setLogDate(LocalDate.now());

                DailyLog log2 = new DailyLog();
                log2.setServiceName("Beard Trim");
                log2.setQuantity(1);
                log2.setAmount(new BigDecimal("50.00"));
                log2.setPaymentType("ONLINE");
                log2.setLogDate(LocalDate.now());

                dailyLogRepo.saveAll(List.of(log1, log2));
            }
            
            System.out.println("✦ VIJAYAN SALON — PostgreSQL Database initialization completed successfully!");
            
        };
    }
}
