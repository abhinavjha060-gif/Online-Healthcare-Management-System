package com.hms.service;

import com.hms.model.Role;
import com.hms.model.User;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorProfileRepository;
import com.hms.repository.MedicalRecordRepository;
import com.hms.repository.PatientProfileRepository;
import com.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Deletes an account together with everything linked to it.
 * Used both by an admin deleting a user and by a user deleting their own account.
 */
@Service
public class UserAccountService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public UserAccountService(UserRepository userRepository,
                               DoctorProfileRepository doctorProfileRepository,
                               PatientProfileRepository patientProfileRepository,
                               AppointmentRepository appointmentRepository,
                               MedicalRecordRepository medicalRecordRepository) {
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * Order matters because of foreign keys:
     * medical records -> appointments -> profile -> user.
     */
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (user.getRole() == Role.DOCTOR) {
            doctorProfileRepository.findByUserId(userId).ifPresent(profile -> {
                medicalRecordRepository.deleteByDoctorId(profile.getId());
                appointmentRepository.deleteByDoctorId(profile.getId());
                doctorProfileRepository.delete(profile);
            });
        } else if (user.getRole() == Role.PATIENT) {
            patientProfileRepository.findByUserId(userId).ifPresent(profile -> {
                medicalRecordRepository.deleteByPatientId(profile.getId());
                appointmentRepository.deleteByPatientId(profile.getId());
                patientProfileRepository.delete(profile);
            });
        }

        userRepository.delete(user);
    }
}
