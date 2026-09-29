package com.vjsalon;

import java.net.URI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vjsalon.model.Models.AdminConfig;
import com.vjsalon.model.Models.ShopStatus;
import com.vjsalon.repository.AdminConfigRepository;
import com.vjsalon.repository.ShopStatusRepository;

@SpringBootApplication
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

        URI uri = URI.create(databaseUrl);
        if (!"postgres".equals(uri.getScheme()) && !"postgresql".equals(uri.getScheme())) {
            throw new IllegalArgumentException("DATABASE_URL must use the postgres or postgresql scheme");
        }
        String jdbcUrl = "jdbc:postgresql://" + uri.getRawAuthority().substring(
                uri.getRawAuthority().lastIndexOf('@') + 1) + uri.getRawPath();
        if (uri.getRawQuery() != null) {
            jdbcUrl += "?" + uri.getRawQuery();
        }
        System.setProperty("spring.datasource.url", jdbcUrl);
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

            System.out.println("VIJAYAN SALON database initialization completed successfully!");
        };
    }
}
