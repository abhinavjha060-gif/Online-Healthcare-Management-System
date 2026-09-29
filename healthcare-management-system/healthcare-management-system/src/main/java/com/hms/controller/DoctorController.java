package com.hms.controller;

import com.hms.model.Appointment;
import com.hms.model.AppointmentStatus;
import com.hms.model.DoctorProfile;
import com.hms.repository.DoctorProfileRepository;
import com.hms.service.AppointmentService;
import com.hms.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    @Autowired
    public DoctorController(DoctorProfileRepository doctorProfileRepository,
                             AppointmentService appointmentService,
                             MedicalRecordService medicalRecordService) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.appointmentService = appointmentService;
        this.medicalRecordService = medicalRecordService;
    }

    private DoctorProfile currentDoctor(Authentication auth) {
        return doctorProfileRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("No doctor profile for this account"));
    }

    /** Runs an appointment action and reports the outcome as a flash message. */
    private String act(RedirectAttributes ra, Runnable action, String successMessage) {
        try {
            action.run();
            ra.addFlashAttribute("success", successMessage);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor/appointments";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        DoctorProfile doctor = currentDoctor(auth);
        var appointments = appointmentService.findForDoctor(doctor.getId());
        model.addAttribute("doctor", doctor);
        model.addAttribute("totalAppointments", appointments.size());
        model.addAttribute("pendingCount", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING).count());
        model.addAttribute("confirmedCount", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED).count());
        model.addAttribute("recentAppointments", appointments.stream().limit(5).toList());
        return "doctor/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(Authentication auth, Model model) {
        DoctorProfile doctor = currentDoctor(auth);
        model.addAttribute("appointments", appointmentService.findForDoctor(doctor.getId()));
        return "doctor/appointments";
    }

    @PostMapping("/appointments/{id}/confirm")
    public String confirm(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        return act(ra, () -> appointmentService.confirmAsDoctor(id, currentDoctor(auth).getId()),
                "Appointment confirmed.");
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        return act(ra, () -> appointmentService.cancelAsDoctor(id, currentDoctor(auth).getId()),
                "Appointment cancelled.");
    }

    @PostMapping("/appointments/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        return act(ra, () -> appointmentService.deleteCancelledAsDoctor(id, currentDoctor(auth).getId()),
                "Cancelled appointment deleted.");
    }

    @GetMapping("/records/new/{appointmentId}")
    public String newRecordForm(@PathVariable Long appointmentId, Authentication auth,
                                 Model model, RedirectAttributes ra) {
        Appointment appointment = appointmentService.findById(appointmentId);
        boolean mine = appointment.getDoctor().getId().equals(currentDoctor(auth).getId());
        if (!mine || appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            ra.addFlashAttribute("error", "You can only add records to your own confirmed appointments.");
            return "redirect:/doctor/appointments";
        }
        model.addAttribute("appointment", appointment);
        return "doctor/record-form";
    }

    @PostMapping("/records/new/{appointmentId}")
    public String saveRecord(@PathVariable Long appointmentId,
                              @RequestParam String diagnosis,
                              @RequestParam String prescription,
                              @RequestParam(required = false) String notes,
                              Authentication auth,
                              RedirectAttributes ra) {
        return act(ra, () -> medicalRecordService.createFromAppointment(
                        appointmentId, currentDoctor(auth).getId(), diagnosis, prescription, notes),
                "Record saved and appointment marked completed.");
    }

    @GetMapping("/records")
    public String records(Authentication auth, Model model) {
        DoctorProfile doctor = currentDoctor(auth);
        model.addAttribute("records", medicalRecordService.findForDoctor(doctor.getId()));
        return "doctor/records";
    }
}
