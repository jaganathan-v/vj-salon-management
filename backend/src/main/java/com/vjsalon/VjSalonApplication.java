package com.vjsalon;

import java.net.URI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vjsalon.model.Models.AdminConfig;
import com.vjsalon.model.Models.ShopStatus;
import com.vjsalon.repository.AdminConfigRepository;
import com.vjsalon.repository.ShopStatusRepository;

@SpringBootApplication
@EnableScheduling
public class VjSalonApplication {

    public static void main(String[] args) {
        configureRenderDatabaseUrl();
        SpringApplication.run(VjSalonApplication.class, args);
    }

    private static void configureRenderDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            return;
        }

        try {
            String clean = databaseUrl.trim();
            if (clean.startsWith("jdbc:")) {
                clean = clean.substring(5);
            }
            URI uri = URI.create(clean);
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
            if (uri.getQuery() != null) {
                jdbcUrl += "?" + uri.getQuery();
            }

            System.setProperty("spring.datasource.url", jdbcUrl);

            String userInfo = uri.getUserInfo();
            if (userInfo != null && !userInfo.isBlank()) {
                String[] parts = userInfo.split(":", 2);
                if (parts.length > 0 && !parts[0].isBlank()) {
                    System.setProperty("spring.datasource.username", parts[0]);
                }
                if (parts.length > 1 && !parts[1].isBlank()) {
                    System.setProperty("spring.datasource.password", parts[1]);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not parse DATABASE_URL: " + e.getMessage());
        }
    }

    @Bean
    public CommandLineRunner dataSeeder(
            ShopStatusRepository shopStatusRepo,
            AdminConfigRepository adminConfigRepo,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (shopStatusRepo.count() == 0) {
                ShopStatus status = new ShopStatus();
                status.setId(1L);
                status.setOpen(true);
                status.setNote("");
                shopStatusRepo.save(status);
            }

            if (adminConfigRepo.count() == 0) {
                AdminConfig admin = new AdminConfig();
                admin.setId(1L);
                admin.setAdminCode("VJADMIN");
                admin.setPasswordHash(passwordEncoder.encode("vj@admin2024"));
                admin.setEmail("admin@vjsalon.com");
                adminConfigRepo.save(admin);
            }

            if (adminConfigRepo.findByAdminCode("admin").isEmpty()) {
                AdminConfig devAdmin = new AdminConfig();
                devAdmin.setId(2L);
                devAdmin.setAdminCode("admin");
                devAdmin.setPasswordHash(passwordEncoder.encode("admin123"));
                devAdmin.setEmail("admin@vjsalon.com");
                adminConfigRepo.save(devAdmin);
            }

            System.out.println("VIJAYAN SALON database initialization completed successfully!");
        };
    }
}
