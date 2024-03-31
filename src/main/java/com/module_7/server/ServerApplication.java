package com.module_7.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.sql.Timestamp;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.module_7.server")
@EntityScan("com.module_7.server")
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
    @Bean
    public CommandLineRunner demoData(ItemsRepository repository) {
        return args -> {
            // Create and save a test item
            ItemsEntity item = new ItemsEntity();
            item.setDescription("Here it is");
            item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            repository.save(item);
        };
    }
}
