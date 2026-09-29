package com.hms.repository;

import com.hms.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUserEmail(String email);
    Optional<DoctorProfile> findByUserId(Long userId);
}
