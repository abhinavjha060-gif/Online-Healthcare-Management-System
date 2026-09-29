package com.hms.controller;

import com.hms.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AuthController {

    private final RegistrationService registrationService;

    @Autowired
    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                            @RequestParam String email,
                            @RequestParam String password,
                            @RequestParam(required = false) String dateOfBirth,
                            @RequestParam(required = false) String phone,
                            @RequestParam(required = false) String bloodGroup,
                            Model model) {
        try {
            LocalDate dob = (dateOfBirth != null && !dateOfBirth.isBlank())
                    ? LocalDate.parse(dateOfBirth) : null;
            registrationService.registerPatient(fullName, email, password, dob, phone, bloodGroup);
            model.addAttribute("success", "Account created! You can now log in.");
            return "login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }
}
