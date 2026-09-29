package com.hms.service;

import com.hms.model.Appointment;
import com.hms.model.AppointmentStatus;
import com.hms.model.DoctorProfile;
import com.hms.model.PatientProfile;
import com.hms.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment book(PatientProfile patient, DoctorProfile doctor, LocalDateTime dateTime, String reason) {
        Appointment appointment = new Appointment(patient, doctor, dateTime, reason);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> findForPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateTimeDesc(patientId);
    }

    public List<Appointment> findForDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateTimeDesc(doctorId);
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAllByOrderByAppointmentDateTimeDesc();
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
    }

    public void save(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    // ---------- Doctor actions (must own the appointment) ----------

    public void confirmAsDoctor(Long appointmentId, Long doctorId) {
        Appointment a = ownedByDoctor(appointmentId, doctorId);
        if (a.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only pending appointments can be confirmed.");
        }
        a.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(a);
    }

    public void cancelAsDoctor(Long appointmentId, Long doctorId) {
        cancel(ownedByDoctor(appointmentId, doctorId));
    }

    public void deleteCancelledAsDoctor(Long appointmentId, Long doctorId) {
        deleteIfCancelled(ownedByDoctor(appointmentId, doctorId));
    }

    // ---------- Patient actions (must own the appointment) ----------

    public void cancelAsPatient(Long appointmentId, Long patientId) {
        cancel(ownedByPatient(appointmentId, patientId));
    }

    public void deleteCancelledAsPatient(Long appointmentId, Long patientId) {
        deleteIfCancelled(ownedByPatient(appointmentId, patientId));
    }

    // ---------- Admin action ----------

    public void deleteCancelled(Long appointmentId) {
        deleteIfCancelled(findById(appointmentId));
    }

    // ---------- helpers ----------

    private Appointment ownedByDoctor(Long appointmentId, Long doctorId) {
        Appointment a = findById(appointmentId);
        if (!a.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("That appointment doesn't belong to you.");
        }
        return a;
    }

    private Appointment ownedByPatient(Long appointmentId, Long patientId) {
        Appointment a = findById(appointmentId);
        if (!a.getPatient().getId().equals(patientId)) {
            throw new IllegalArgumentException("That appointment doesn't belong to you.");
        }
        return a;
    }

    private void cancel(Appointment a) {
        if (a.getStatus() != AppointmentStatus.PENDING && a.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed appointments can be cancelled.");
        }
        a.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(a);
    }

    private void deleteIfCancelled(Appointment a) {
        if (a.getStatus() != AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Only cancelled appointments can be deleted.");
        }
        appointmentRepository.delete(a);
    }
}
