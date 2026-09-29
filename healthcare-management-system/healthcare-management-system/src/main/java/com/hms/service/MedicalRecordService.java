package com.hms.service;

import com.hms.model.*;
import com.hms.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentService appointmentService;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                 AppointmentService appointmentService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentService = appointmentService;
    }

    @Transactional
    public MedicalRecord createFromAppointment(Long appointmentId, Long doctorId, String diagnosis,
                                                String prescription, String notes) {
        Appointment appointment = appointmentService.findById(appointmentId);

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("That appointment doesn't belong to you.");
        }
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Records can only be added to confirmed appointments.");
        }

        MedicalRecord record = new MedicalRecord();
        record.setAppointment(appointment);
        record.setPatient(appointment.getPatient());
        record.setDoctor(appointment.getDoctor());
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setNotes(notes);
        medicalRecordRepository.save(record);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentService.save(appointment);

        return record;
    }

    public List<MedicalRecord> findForPatient(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public List<MedicalRecord> findForDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }
}
