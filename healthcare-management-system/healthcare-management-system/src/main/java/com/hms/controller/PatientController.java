package com.hms.controller;

import com.hms.model.PatientProfile;
import com.hms.repository.DoctorProfileRepository;
import com.hms.repository.PatientProfileRepository;
import com.hms.service.AppointmentService;
import com.hms.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    @Autowired
    public PatientController(PatientProfileRepository patientProfileRepository,
                              DoctorProfileRepository doctorProfileRepository,
                              AppointmentService appointmentService,
                              MedicalRecordService medicalRecordService) {
        this.patientProfileRepository = patientProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.appointmentService = appointmentService;
        this.medicalRecordService = medicalRecordService;
    }

    private PatientProfile currentPatient(Authentication auth) {
        return patientProfileRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("No patient profile for this account"));
    }

    /** Runs an appointment action and reports the outcome as a flash message. */
    private String act(RedirectAttributes ra, Runnable action, String successMessage) {
        try {
            action.run();
            ra.addFlashAttribute("success", successMessage);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/patient/appointments";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        PatientProfile patient = currentPatient(auth);
        var appointments = appointmentService.findForPatient(patient.getId());
        model.addAttribute("patient", patient);
        model.addAttribute("totalAppointments", appointments.size());
        model.addAttribute("recentAppointments", appointments.stream().limit(5).toList());
        return "patient/dashboard";
    }

    @GetMapping("/appointments/new")
    public String bookForm(Model model) {
        model.addAttribute("doctors", doctorProfileRepository.findAll());
        return "patient/book-appointment";
    }

    @PostMapping("/appointments/new")
    public String book(Authentication auth,
                        @RequestParam Long doctorId,
                        @RequestParam String appointmentDateTime,
                        @RequestParam String reason,
                        Model model) {
        try {
            PatientProfile patient = currentPatient(auth);
            var doctor = doctorProfileRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Selected doctor not found"));
            LocalDateTime dateTime = LocalDateTime.parse(appointmentDateTime);
            appointmentService.book(patient, doctor, dateTime, reason);
            return "redirect:/patient/appointments";
        } catch (Exception ex) {
            model.addAttribute("doctors", doctorProfileRepository.findAll());
            model.addAttribute("error", "Could not book appointment: " + ex.getMessage());
            return "patient/book-appointment";
        }
    }

    @GetMapping("/appointments")
    public String appointments(Authentication auth, Model model) {
        PatientProfile patient = currentPatient(auth);
        model.addAttribute("appointments", appointmentService.findForPatient(patient.getId()));
        return "patient/appointments";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        return act(ra, () -> appointmentService.cancelAsPatient(id, currentPatient(auth).getId()),
                "Appointment cancelled.");
    }

    @PostMapping("/appointments/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        return act(ra, () -> appointmentService.deleteCancelledAsPatient(id, currentPatient(auth).getId()),
                "Cancelled appointment deleted.");
    }

    @GetMapping("/records")
    public String records(Authentication auth, Model model) {
        PatientProfile patient = currentPatient(auth);
        model.addAttribute("records", medicalRecordService.findForPatient(patient.getId()));
        return "patient/records";
    }
}
