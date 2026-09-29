package com.hms.repository;

import com.hms.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    Optional<PatientProfile> findByUserEmail(String email);
    Optional<PatientProfile> findByUserId(Long userId);
}
