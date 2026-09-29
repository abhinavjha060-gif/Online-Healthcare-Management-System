package com.hms.service;

import com.hms.model.PatientProfile;
import com.hms.model.Role;
import com.hms.model.User;
import com.hms.repository.PatientProfileRepository;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Handles self-service registration. Only PATIENT accounts can self-register;
 * Admin and Doctor accounts are provisioned by an administrator.
 */
@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository,
                                PatientProfileRepository patientProfileRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerPatient(String fullName, String email, String rawPassword,
                                 LocalDate dob, String phone, String bloodGroup) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        User user = new User(fullName, email, passwordEncoder.encode(rawPassword), Role.PATIENT);
        userRepository.save(user);

        PatientProfile profile = new PatientProfile(user, dob, phone, bloodGroup);
        patientProfileRepository.save(profile);

        return user;
    }
}
