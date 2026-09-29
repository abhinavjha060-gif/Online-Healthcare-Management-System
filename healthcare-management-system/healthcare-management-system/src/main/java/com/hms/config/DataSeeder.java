package com.hms.config;

import com.hms.model.*;
import com.hms.repository.DoctorProfileRepository;
import com.hms.repository.PatientProfileRepository;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds the database with one demo account per role the first time the app runs,
 * so you can log in and demonstrate the system immediately without any manual setup.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataSeeder(UserRepository userRepository,
                       DoctorProfileRepository doctorProfileRepository,
                       PatientProfileRepository patientProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // already seeded
        }

        User admin = new User("System Admin", "admin@hms.com",
                passwordEncoder.encode("admin123"), Role.ADMIN);
        userRepository.save(admin);

        User doctorUser = new User("Dr. Asha Verma", "doctor@hms.com",
                passwordEncoder.encode("doctor123"), Role.DOCTOR);
        userRepository.save(doctorUser);
        doctorProfileRepository.save(new DoctorProfile(doctorUser, "General Physician", "9876500001"));

        User doctorUser2 = new User("Dr. Rohit Malhotra", "doctor2@hms.com",
                passwordEncoder.encode("doctor123"), Role.DOCTOR);
        userRepository.save(doctorUser2);
        doctorProfileRepository.save(new DoctorProfile(doctorUser2, "Cardiologist", "9876500002"));

        User patientUser = new User("Rahul Sharma", "patient@hms.com",
                passwordEncoder.encode("patient123"), Role.PATIENT);
        userRepository.save(patientUser);
        patientProfileRepository.save(new PatientProfile(patientUser,
                LocalDate.of(2000, 5, 12), "9876500099", "O+"));

        System.out.println("\n================ DEMO LOGIN CREDENTIALS ================");
        System.out.println(" Admin   -> admin@hms.com    / admin123");
        System.out.println(" Doctor  -> doctor@hms.com   / doctor123  (General Physician)");
        System.out.println(" Doctor  -> doctor2@hms.com  / doctor123  (Cardiologist)");
        System.out.println(" Patient -> patient@hms.com  / patient123");
        System.out.println(" (New patients can also self-register at /register)");
        System.out.println("==========================================================\n");
    }
}
