package com.hms.service;

import com.hms.model.DoctorProfile;
import com.hms.model.Role;
import com.hms.model.User;
import com.hms.repository.DoctorProfileRepository;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(UserRepository userRepository,
                         DoctorProfileRepository doctorProfileRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    @Transactional
    public DoctorProfile createDoctor(String fullName, String email, String rawPassword,
                                       String specialization, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        User user = new User(fullName, email, passwordEncoder.encode(rawPassword), Role.DOCTOR);
        userRepository.save(user);

        DoctorProfile profile = new DoctorProfile(user, specialization, phone);
        return doctorProfileRepository.save(profile);
    }

    public long countByRole(Role role) {
        return userRepository.findByRole(role).size();
    }
}
