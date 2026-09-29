package com.hms.repository;

import com.hms.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<MedicalRecord> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    // Used when an account is deleted. Must be called inside a transaction.
    void deleteByDoctorId(Long doctorId);
    void deleteByPatientId(Long patientId);
}
