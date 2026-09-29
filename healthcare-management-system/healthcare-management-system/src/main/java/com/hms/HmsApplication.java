package com.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmsApplication.class, args);
        System.out.println("\n===============================================");
        System.out.println(" Online Healthcare Management System is running");
        System.out.println(" Open: http://localhost:8080");
        System.out.println("===============================================\n");
    }
}
