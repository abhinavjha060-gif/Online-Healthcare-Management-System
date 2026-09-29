package com.hms.repository;

import com.hms.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdOrderByAppointmentDateTimeDesc(Long doctorId);
    List<Appointment> findByPatientIdOrderByAppointmentDateTimeDesc(Long patientId);
    List<Appointment> findAllByOrderByAppointmentDateTimeDesc();

    // Used when an account is deleted. Must be called inside a transaction.
    void deleteByDoctorId(Long doctorId);
    void deleteByPatientId(Long patientId);
}
