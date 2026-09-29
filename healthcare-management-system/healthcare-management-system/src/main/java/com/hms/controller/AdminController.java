package com.hms.controller;

import com.hms.model.Role;
import com.hms.model.User;
import com.hms.service.AdminService;
import com.hms.service.AppointmentService;
import com.hms.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final AppointmentService appointmentService;
    private final UserAccountService userAccountService;

    @Autowired
    public AdminController(AdminService adminService,
                            AppointmentService appointmentService,
                            UserAccountService userAccountService) {
        this.adminService = adminService;
        this.appointmentService = appointmentService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", adminService.findAllUsers().size());
        model.addAttribute("totalDoctors", adminService.countByRole(Role.DOCTOR));
        model.addAttribute("totalPatients", adminService.countByRole(Role.PATIENT));
        model.addAttribute("totalAppointments", appointmentService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminService.findAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            User target = adminService.findUser(id);
            if (target.getEmail().equals(auth.getName())) {
                ra.addFlashAttribute("error", "You can't delete the account you're logged in with.");
                return "redirect:/admin/users";
            }
            userAccountService.deleteAccount(id);
            ra.addFlashAttribute("success",
                    "Deleted " + target.getFullName() + " and all linked appointments and records.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to delete user {}", id, ex);
            ra.addFlashAttribute("error", "Could not delete this user. Check the server log for details.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/doctors/new")
    public String newDoctorForm() {
        return "admin/new-doctor";
    }

    @PostMapping("/doctors/new")
    public String createDoctor(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String specialization,
                                @RequestParam(required = false) String phone,
                                Model model) {
        try {
            adminService.createDoctor(fullName, email, password, specialization, phone);
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "admin/new-doctor";
        }
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        model.addAttribute("appointments", appointmentService.findAll());
        return "admin/appointments";
    }

    @PostMapping("/appointments/{id}/delete")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes ra) {
        try {
            appointmentService.deleteCancelled(id);
            ra.addFlashAttribute("success", "Cancelled appointment deleted.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/appointments";
    }
}
